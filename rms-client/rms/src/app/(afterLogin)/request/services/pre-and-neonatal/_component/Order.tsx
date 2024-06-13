'use client';

import style from "@/app/(afterLogin)/request/services/pre-and-neonatal/_component/order.module.css";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import DownloadExcelButton from "@/app/(afterLogin)/request/services/pre-and-neonatal/_component/DownloadExcelButton";
import UploadExcelButton from "@/app/(afterLogin)/request/services/pre-and-neonatal/_component/UploadExcelButton";
import React, {useState} from "react";
import {format} from "date-fns";
import {putRequest} from "@/app/(afterLogin)/request/services/pre-and-neonatal/_api/putRequest";

type RequestData = {
    registrationDate: string; // 등록일자
    ward: string;             // 병동
    patientName: string;      // 환자 이름
    personalID: string;       // 주민등록번호
    gender: string;           // 성별
    physician: string;        // 의사 이름
    collectionDate: string;   // 채취일자
    chartNumber: string;      // 차트 번호
    code: string;             // 코드
    gestationalAge: string;   // 임신 기간
    weight: string;           // 몸무게
    fetuses: string;          // 태아 수
    quantity: number;         // 샘플 수
    notes: string;            // 메모
    race: string;             // 인종
};

export default function Order() {

    const transformDataToFormat = (data: RequestData[], status: string): any => {
        return data.map((item) => {
            const birthDate = new Date(item.personalID);
            const birthYear = birthDate.getFullYear();
            const birthMonth = birthDate.getMonth() + 1;
            const birthDay = birthDate.getDate() + 1;

            return {
                service: {
                    id: item.code
                },
                memo: item.notes,
                ward: item.ward,
                physician: item.physician,
                status: status,
                sample: {
                    quantity: item.quantity,
                    sampling_on: item.collectionDate,
                    sample_type: {
                        id: "2"
                    },
                    patient: {
                        serial: item.chartNumber,
                        sex: item.gender,
                        name: item.patientName,
                        birth_year: birthYear,
                        birth_month: birthMonth,
                        birth_day: birthDay,
                        organization: {
                            id: "test1234",
                            name: "patchname12345123"
                        }
                    },
                    extensions: [
                        {
                            id: "TA0003",
                            value: item.gestationalAge
                        },
                        {
                            id: "TA0004",
                            value: item.fetuses
                        },
                        {
                            id: "TA0007",
                            value: "quad"
                        },
                        {
                            id: "TA0008",
                            value: 3
                        }
                    ]
                }
            };
        })
    };

    const handleOrderNowClick = async () => {
        const orderData = transformDataToFormat(requestData, "ORDERED");
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
            console.log(orderData);
            console.error("Error placing order:", error);
            alert("An error occurred while placing the order.");
        }
    };

    const handleAddToCartClick = async () => {
        const cartData = transformDataToFormat(requestData, "CART");
        try {
            const response = await putRequest(cartData);
            if (response.ok) {
                alert("successfully!");
            } else {
                alert(cartData);
            }
        } catch (error) {
            alert(error);
        }
    };


    const [requestData, setRequestData] = useState<RequestData[]>([]);

    function convertExcelSerialToDate(serial: number): Date {
        const excelEpochInMs = new Date(1899, 11, 30).getTime();
        const dateInMs = excelEpochInMs + (serial - 1) * 24 * 60 * 60 * 1000;

        return new Date(dateInMs);
    }

    const handleFileUpload = (jsonData: (string | number)[][]) => {
        const headers = jsonData[0] as string[];
        const rows = jsonData.slice(1).map(row => {
            let rowData: Partial<RequestData> = {};
            headers.forEach((header, index) => {
                const value = row[index];
                if (header === 'registrationDate' || header === 'personalID' || header === 'collectionDate') {
                    if (typeof value === 'number') {
                        const date = convertExcelSerialToDate(value);
                        (rowData as any)[header] = date;
                    } else {
                        (rowData as any)[header] = value;
                    }
                } else {
                    (rowData as any)[header] = value;
                }
            });
            return rowData as RequestData;
        });
        setRequestData(rows);
    };

    return (
        <>
            <div className={style.top}>
                <div className={style.downloadButton}>
                    <DownloadExcelButton/>
                    <UploadExcelButton onFileUpload={handleFileUpload} />
                </div>
                <div className={style.orderAndCartButton}>
                    <GreenButton name={"Add to Cart"} onClick={handleAddToCartClick}/>
                    <BlueButton name={"Order Now"} onClick={handleOrderNowClick}/>
                </div>
            </div>
            <div>
                <div className={style.container}>
                    <table className={style.table}>
                        <thead>
                        <tr>
                            <th>Registration Date<br/>(YYYY/MM/DD)</th>
                            <th>Ward</th>
                            <th>Patient Name</th>
                            <th>Personal ID Number<br/>(YYYY/MM/DD)</th>
                            <th>Gender<br/>(Male, Female)</th>
                            <th>Physician</th>
                            <th>Collection Date<br/>(YYYY/MM/DD)</th>
                            <th>Chart Number</th>
                            <th>Code</th>
                            <th>Gestational Age</th>
                            <th>Weight</th>
                            <th>Fetuses<br/>(1 or 2)</th>
                            <th>Quantity</th>
                            <th>Notes</th>
                            <th>Race<br/>(Genome Health Premium)</th>
                        </tr>
                        </thead>
                        <tbody>
                        {requestData.map((row, rowIndex) => (
                            <tr key={rowIndex}>
                                <td>{row.registrationDate ? format(new Date(row.registrationDate), "yyyy/MMM/dd") : '-'}</td>
                                <td>{row.ward}</td>
                                <td>{row.patientName}</td>
                                <td>{row.personalID ? format(new Date(row.personalID), "yyyy/MMM/dd") : '-'}</td>
                                <td>{row.gender}</td>
                                <td>{row.physician}</td>
                                <td>{row.collectionDate ? format(new Date(row.collectionDate), "yyyy/MMM/dd") : '-'}</td>
                                <td>{row.chartNumber}</td>
                                <td>{row.code}</td>
                                <td>{row.gestationalAge}</td>
                                <td>{row.weight}</td>
                                <td>{row.fetuses}</td>
                                <td>{row.quantity}</td>
                                <td>{row.notes}</td>
                                <td>{row.race}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </>
    )
}