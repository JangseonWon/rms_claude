'use client';

import style from "@/app/(afterLogin)/request/services/[service]/multi/_component/order.module.css";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import UploadExcelButton from "@/app/(afterLogin)/request/services/[service]/multi/_component/UploadExcelButton";
import React, {useCallback, useEffect, useState} from "react";
import {format} from "date-fns";
import {putRequest} from "@/app/(afterLogin)/request/services/[service]/multi/_api/putRequest";
import {
    useOkNotice,
    useOpenNoticeDialog,
    useSetMessageNoticeDialog,
    useSetOkNotice
} from "@/store/useNoticeDialogStore";
import {Extension} from "@/model/Extension";
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {usePathname} from "next/navigation";
import DownloadExcelButton from "@/app/(afterLogin)/request/services/[service]/multi/_component/DownloadExcelButton";

type RequestData = {
    sampleType: string;       // 샘플 타입
    institution: string;      // 기관
    registrationDate: string; // 등록일자
    ward: string;             // 병동
    patientName: string;      // 환자 이름
    personalID: string;       // 주민등록번호
    gender: string;           // 성별
    physician: string;        // 의사 이름
    medicalDepartment: string;// 병원명
    collectionDate: string;   // 채취일자
    chartNumber: string;      // 차트 번호
    code: string;             // 코드
    gestationalAge: string;   // 임신 기간
    weight: string;           // 몸무게
    fetuses: string;          // 태아 수
    quantity: number;         // 샘플 수
    notes: string;            // 메모
    race: string;             // 인종
    extensions: Record<string, string | number | boolean>;
};

const excelBaseDate = new Date(1899, 11, 30);

export default function Order() {
    const setShowNoticeDialog = useOpenNoticeDialog();
    const setNoticeMessage = useSetMessageNoticeDialog();
    const okNotice = useOkNotice();
    const setOkNotice = useSetOkNotice();
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [requestData, setRequestData] = useState<RequestData[]>([]);
    const [extensions, setExtensions] = useState<Extension[]>([]);

    const fetchExtensions = async () => {
        const response = await fetchServiceExtensions(serviceId);
        setExtensions(response as Extension[]);
    };

    const handleFileUpload = useCallback((data: any[]) => {
        // 헤더를 별도로 분리하고 나머지 데이터와 매핑
        const headers = data[0];
        const bodyData = data.slice(1);

        const mappedData = bodyData.map((row: any) => {
            const mapByHeader = (header: string) => {
                const index = headers.indexOf(header);
                return index !== -1 ? row[index] : undefined;
            };

            const excelToDate = (excelDate: number) => {
                return new Date(excelBaseDate.getTime() + excelDate * 86400000);
            };

            const registrationDate = mapByHeader("Registration Date")
                ? format(excelToDate(mapByHeader("Registration Date")), 'yyyy-MM-dd')
                : 'Non';

            const collectionDate = mapByHeader("Collection Date")
                ? format(excelToDate(mapByHeader("Collection Date")), 'yyyy-MM-dd')
                : 'Non';

            const personalID = mapByHeader("Personal ID Number")
                ? format(excelToDate(mapByHeader("Personal ID Number")), 'yyyy-MM-dd')
                : 'Non';

            // 확장 필드 처리
            const extensionFields = headers.slice(14);
            const extensions = extensionFields.reduce((acc: Record<string, string | number | boolean>, field: string, idx: number) => {
                const value = row[14 + idx];
                acc[field] = value === false || value ? value : '';
                return acc;
            }, {});

            return {
                sampleType: mapByHeader("Sample Type"),
                institution: mapByHeader("Institution"),
                registrationDate,
                ward: mapByHeader("Ward"),
                patientName: mapByHeader("Patient Name"),
                personalID,
                gender: mapByHeader("Gender"),
                physician: mapByHeader("Physician"),
                medicalDepartment: mapByHeader("Medical Department"),
                collectionDate,
                chartNumber: mapByHeader("Chart Number"),
                code: mapByHeader("Code"),
                gestationalAge: mapByHeader("Gestational Age"),
                weight: mapByHeader("Weight"),
                fetuses: mapByHeader("Fetuses"),
                quantity: mapByHeader("Quantity"),
                notes: mapByHeader("Notes"),
                race: mapByHeader("Race (Genome Health Premium)"),
                extensions
            };
        });

        setRequestData(mappedData);
    }, [extensions]);

    const transformDataToFormat = (data: RequestData[], status: string): any => {
        return data.map((item) => {
            const birthDate = new Date(item.personalID);
            const birthYear = birthDate.getFullYear();
            const birthMonth = birthDate.getMonth() + 1;
            const birthDay = birthDate.getDate();

            const currentDate = new Date();
            let age = currentDate.getFullYear() - birthYear;

            if (
                currentDate.getMonth() + 1 < birthMonth ||
                (currentDate.getMonth() + 1 === birthMonth && currentDate.getDate() < birthDay)
            ) {
                age--;
            }

            const [sampleTypeId, sampleTypeName] = item.sampleType.split('/');
            const [institutionId, institutionName] = item.institution.split('/');

            const extensionData = extensions.map(extension => ({
                id: extension.id,
                value: item.extensions[extension.name!]
            }));

            const sex = item.gender === "Male" ? "M" : item.gender === "Female" ? "F" : item.gender;

            return {
                service: {
                    id: serviceId
                },
                memo: item.notes,
                ward: item.ward,
                physician: item.physician,
                status: status,
                department: item.medicalDepartment,
                sample: {
                    quantity: item.quantity,
                    age: age.toString(),
                    sampling_on: item.collectionDate,
                    sample_type: {
                        id: sampleTypeId,
                        name: sampleTypeName
                    },
                    patient: {
                        serial: item.chartNumber,
                        sex: sex,
                        name: item.patientName,
                        birth_year: birthYear,
                        birth_month: birthMonth,
                        birth_day: birthDay,
                        organization: {
                            id: institutionId,
                            name: institutionName
                        }
                    },
                    extensions: extensionData
                }
            };
        })
    };

    const handleOrderNowClick = async () => {
        const orderData = transformDataToFormat(requestData, "UNCONFIRMED_ORDER");

        try {
            const response = await putRequest(orderData);
            if (response.ok) {
                alert("Order placed successfully!");
                console.log(orderData);
            } else {
                console.log(orderData);
                alert("Failed to place the order.");
            }
        } catch (error) {
            console.error("Error placing order:", error);
            alert("An error occurred while placing the order.");
        }
    };

    const handleConfirmedAddToCart = useCallback(async () => {
        try {
            const cartData = transformDataToFormat(requestData, "CART");
            const response = await putRequest(cartData);
            if (response.ok) {
                alert("successfully!");
            } else {
                alert(cartData);
            }
        } catch (error) {
            alert(error);
        }
    }, [requestData]);

    const handleAddToCartClick = () => {
        setShowNoticeDialog(true);
        setNoticeMessage('Do you want to add items to the cart?');
    };


    useEffect(()=> {
        fetchExtensions();
    }, []);

    useEffect(() => {
        if (okNotice) {
            handleConfirmedAddToCart();
            setOkNotice(false);
        }
    }, [okNotice, handleConfirmedAddToCart, setOkNotice]);

    const formatNotes = (text: string | undefined) => {
        if (!text) return '';
        return String(text).replace(/\r\n|\n|\r/g, '<br />');
    };

    const formatBooleanValue = (value: string | number | boolean) => {
        if (value === undefined || value === null) return '-';
        return typeof value === 'boolean' ? (value ? 'TRUE' : 'FALSE') : value;
    };

    return (
        <>
            <div className={style.top}>
                <div className={style.downloadButton}>
                    <DownloadExcelButton extensions={extensions}/>
                    <UploadExcelButton onFileUpload={handleFileUpload} />
                </div>
                <div className={style.orderAndCartButton}>
                    <GreenButton name={"Add to Cart"} onClick={handleAddToCartClick}/>
                    <BlueButton name={"Order Now"} onClick={handleOrderNowClick}/>
                </div>
            </div>
            <div className={style.container}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th className={style.topHeader}>Sample Type</th>
                        <th className={style.topHeader}>Institution</th>
                        <th className={style.extensionHeader}>Registration Date</th>
                        <th className={style.extensionHeader}>Ward</th>
                        <th className={style.extensionHeader}>Patient Name</th>
                        <th className={style.extensionHeader}>Personal ID Number</th>
                        <th className={style.extensionHeader}>Gender</th>
                        <th className={style.extensionHeader}>Physician</th>
                        <th className={style.extensionHeader}>Medical Department</th>
                        <th className={style.extensionHeader}>Collection Date</th>
                        <th className={style.extensionHeader}>Chart Number</th>
                        <th className={style.extensionHeader}>Gestational Age</th>
                        <th className={style.extensionHeader}>Weight</th>
                        <th className={style.extensionHeader}>Fetuses</th>
                        <th className={style.extensionHeader}>Quantity</th>
                        <th className={style.notesHeader}>Notes</th>
                        <th className={style.extensionHeader}>Race<br/>(Genome Health Premium)</th>
                        {extensions.map((extension, index) => (
                            <th className={style.extensionHeader} key={index}>{extension.name}</th>
                        ))}
                    </tr>
                    </thead>
                    <tbody>
                    {requestData.map((item, index) => (
                        <tr key={index}>
                            <td>{item.sampleType || ''}</td>
                            <td>{item.institution || ''}</td>
                            <td>{item.registrationDate || ''}</td>
                            <td>{item.ward || ''}</td>
                            <td>{item.patientName || ''}</td>
                            <td>{item.personalID || ''}</td>
                            <td>{item.gender || ''}</td>
                            <td>{item.physician || ''}</td>
                            <td>{item.medicalDepartment || ''}</td>
                            <td>{item.collectionDate || ''}</td>
                            <td>{item.chartNumber || ''}</td>
                            <td>{item.gestationalAge || ''}</td>
                            <td>{item.weight || ''}</td>
                            <td>{item.fetuses || ''}</td>
                            <td>{item.quantity || ''}</td>
                            <td dangerouslySetInnerHTML={{__html: formatNotes(item.notes)}}/>
                            <td>{item.race || ''}</td>
                            {extensions.map((extension, extIndex) => (
                                <td key={extIndex}>
                                    {item.extensions && item.extensions[extension.name!] !== undefined
                                        ? formatBooleanValue(item.extensions[extension.name!])
                                        : '-'}
                                </td>
                            ))}
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </>
    )
}