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
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

type RequestData = {
    sampleType: string;       // 샘플 타입
    institution: string;      // 기관
    birth: string;            // 생일
    patientName: string;      // 환자 이름
    gender: string;              // 성별
    physician: string;        // 의사 이름
    medicalDepartment: string;// 병원명
    collectionDate: string;   // 채취일자
    mrn: string;              // MRN
    quantity: number;         // 샘플 수
    memo: string;            // 메모
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
    const showAlert = CallAlertDialog();
    const [requestData, setRequestData] = useState<RequestData[]>([]);
    const [extensions, setExtensions] = useState<Extension[]>([]);

    const fetchExtensions = async () => {
        const response = await fetchServiceExtensions(serviceId);
        if(response.ok){
            setExtensions(await response.json() as Extension[]);
        }else{
            showAlert("Error!")
        }
    };

    const handleFileUpload = useCallback((data: any[]) => {
        const headers = data[0];
        const bodyData = data.slice(1);

        const mappedData = bodyData.map((row: any) => {
            const mapByHeader = (header: string) => {
                const index = headers.indexOf(header);
                return index !== -1 ? row[index] : undefined;
            };
            const extensionFields = headers.slice(11);
            const extensions = extensionFields.reduce((acc: Record<string, string | number | boolean>, field: string, idx: number) => {
                const value = row[11 + idx];
                acc[field] = value === false || value ? value : '';
                return acc;
            }, {});

            const excelToDate = (excelDate: number) => {
                return new Date(excelBaseDate.getTime() + excelDate * 86400000);
            };

            const birth = mapByHeader("Date of Birth")
                ? format(excelToDate(mapByHeader("Date of Birth")), 'yyyy-MM-dd')
                : '-';
            const collectionDate = mapByHeader("Date of Collection")
                ? format(excelToDate(mapByHeader("Date of Collection")), 'yyyy-MM-dd')
                : '-';
            return {
                sampleType: mapByHeader("Sample Type"),
                institution: mapByHeader("Institution Name"),
                birth: birth,
                mrn: mapByHeader("MRN"),
                patientName: mapByHeader("Patient Name"),
                gender: mapByHeader("Gender"),
                physician: mapByHeader("Physician Name"),
                medicalDepartment: mapByHeader("Medical Department"),
                collectionDate: collectionDate,
                code: mapByHeader("Code"),
                quantity: mapByHeader("Quantity"),
                memo: mapByHeader("Memo"),
                extensions
            };
        });

        setRequestData(mappedData);
    }, [extensions]);

    const transformDataToFormat = (data: RequestData[], status: string): any => {
        const missingFields: string[] = [];

        const transformedData = data.filter((item) => item.institution && item.institution.includes('/'))
            .map((item) => {
                const birthDate = new Date(item.birth);
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

                const [sampleTypeId, sampleTypeName] = item.sampleType?.split('/') || ["", ""];
                const [institutionId, institutionName] = item.institution?.split('/') || ["", ""];

                const checkMissingField = (field: string | number, fieldName: string) => {
                    if (field === '-' && !missingFields.includes(fieldName) ||
                        !field && !missingFields.includes(fieldName)) {
                        missingFields.push(fieldName);
                    }
                };

                checkMissingField(item.patientName, "patient name");
                checkMissingField(item.mrn, "mrn");
                checkMissingField(item.gender, "gender");
                checkMissingField(item.sampleType, "sample type");
                checkMissingField(item.collectionDate, "date of collection");
                checkMissingField(item.quantity, "quantity");

                const extensionData = extensions.map(extension => {
                    const value = item.extensions[extension.name!];

                    if (extension.required && (value === undefined || value === null || value === "")) {
                        if (!missingFields.includes(extension.name!)) {
                            missingFields.push(extension.name!);
                        }
                    }

                    return {
                        id: extension.id,
                        value: value
                    };
                });

                const sex = item.gender === "Male" ? "M" : item.gender === "Female" ? "F" : item.gender;

                return {
                    service: {
                        id: serviceId
                    },
                    memo: item.memo,
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
                            serial: item.mrn,
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
            });

        if (missingFields.length > 0) {
            showAlert(`Error: The following required fields are missing values:
            \n${missingFields.join(', ')}. 
            \nPlease provide values.`);
            return null;
        }

        return transformedData;
    };

    const handleOrderNowClick = async () => {
        if (requestData.length === 0) {
            showAlert("Error: No data available for ordering.");
            return;
        }

        const orderData = transformDataToFormat(requestData, "UNCONFIRMED_ORDER");

        if (!orderData) {
            return;
        }

        try {
            const response = await putRequest(orderData);
            if (response.ok) {
                showAlert("Order placed successfully!", true);
                setRequestData([]);
            } else {
                showAlert("Failed to place the order.");
            }
        } catch (error) {
            showAlert("An error occurred while placing the order.");
        }
    };

    const handleConfirmedAddToCart = useCallback(async () => {
        if (requestData.length === 0) {
            showAlert("Error: No data available to add to cart.");
            return;
        }

        const cartData = transformDataToFormat(requestData, "CART");

        if (!cartData) {
            return;
        }

        try {
            const response = await putRequest(cartData);
            if (response.ok) {
                showAlert("Add Cart successfully!", true);
                setRequestData([]);
            } else {
                showAlert("Failed to place the cart.");
            }
        } catch (error) {
            showAlert("An error occurred while placing the cart.");
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
                        <th className={style.header200}>Institution Name</th>
                        <th className={style.header150}>Patient Name</th>
                        <th className={style.header100}>MRN</th>
                        <th className={style.header150}>Date of Birth</th>
                        <th className={style.header100}>Gender</th>
                        <th className={style.header150}>Sample Type</th>
                        <th className={style.header150}>Date of Collection</th>
                        <th className={style.header100}>Quantity</th>
                        <th className={style.header150}>Medical Department</th>
                        <th className={style.header150}>Physician Name</th>
                        <th className={style.notesHeader}>Memo</th>
                        {extensions.map((extension, index) => (
                            <th className={style.header100} key={index}>{extension.name}</th>
                        ))}
                    </tr>
                    </thead>
                    <tbody>
                    {requestData.filter((item) => item.institution && item.institution.includes('/'))
                        .map((item, index) => (
                        <tr key={index}>
                            <td>{item.institution && item.institution.includes('/') ? item.institution.split('/')[1] : '-'}</td>
                            <td>{item.patientName || '-'}</td>
                            <td>{item.mrn || '-'}</td>
                            <td>{item.birth || '-'}</td>
                            <td>{item.gender || '-'}</td>
                            <td>{item.sampleType && item.sampleType.includes('/') ? item.sampleType.split('/')[1] : '-'}</td>
                            <td>{item.collectionDate || '-'}</td>
                            <td>{item.quantity || '-'}</td>
                            <td>{item.medicalDepartment || '-'}</td>
                            <td>{item.physician || '-'}</td>
                            <td dangerouslySetInnerHTML={{__html: formatNotes(item.memo)}}/>
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