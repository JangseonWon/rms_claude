'use client';

import style from "@/app/(afterLogin)/request/services/pre-and-neonatal/_component/order.module.css";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import DownloadExcelButton from "@/app/(afterLogin)/request/services/pre-and-neonatal/_component/DownloadExcelButton";
import UploadExcelButton from "@/app/(afterLogin)/request/services/pre-and-neonatal/_component/UploadExcelButton";
import React, {useState} from "react";

type RequestData = {
    Institution: string;
    patientName: string;
    patientBOD: string;
    gender: string;
    physicianName: string;
    collectionDate: string;
    mrn: string;
    serviceCode: string;
    pregnant: string;
    weight: string;
    fetus: string;
};

export default function Order() {

    const handleAddToCartClick = () => {
        alert("cart");
    }

    const handleOrderNowClick = () => {
        alert("order");
    }

    const [requestData, setRequestData] = useState<RequestData[]>([]);

    const handleFileUpload = (jsonData: (string | number)[][]) => {
        const headers = jsonData[0] as string[];
        const rows = jsonData.slice(1).map(row => {
            let rowData: Partial<RequestData> = {};
            headers.forEach((header, index) => {
                (rowData as any)[header] = row[index] as string | number;
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
                            <th>Institution</th>
                            <th>Patient(s) Name</th>
                            <th>Patient BOD<br/>(YYYY/MM/DD)</th>
                            <th>Gender</th>
                            <th>Physician<br/>Name</th>
                            <th>Collection Date<br/>(YYYY/MM/DD)</th>
                            <th>MRN</th>
                            <th>Service<br/>Code</th>
                            <th>Pregnant<br/>(Week + day)</th>
                            <th>Weight</th>
                            <th>Fetus</th>
                        </tr>
                        </thead>
                        <tbody>
                        {requestData.map((row, rowIndex) => (
                            <tr key={rowIndex}>
                                <td>{row.Institution}</td>
                                <td>{row.patientName}</td>
                                <td>{row.patientBOD}</td>
                                <td>{row.gender}</td>
                                <td>{row.physicianName}</td>
                                <td>{row.collectionDate}</td>
                                <td>{row.mrn}</td>
                                <td>{row.serviceCode}</td>
                                <td>{row.pregnant}</td>
                                <td>{row.weight}</td>
                                <td>{row.fetus}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </>
    )
}