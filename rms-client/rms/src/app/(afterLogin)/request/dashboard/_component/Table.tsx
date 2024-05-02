"use client"

import {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/dashboard/_component/table.module.css"
import {getStatisticsRequest} from "@/app/(afterLogin)/request/dashboard/_api/getStatisticsRequest";
import type {Request} from "@/model/Request";
import Loading from "@/app/(afterLogin)/_component/Loading";

export default function Statistics() {
    const [statisticsData, setStatisticsData] = useState<Request>()
    const tableData = [
        {
            registrationId: "20240104-971-5005",
            userName: "Fortress",
            institution: "VINMEC",
            service: "WES",
            patientName: "Leyla Bayram",
            mrn: "G-10237",
            patientBod: "2024-04-01",
            currentStatus: "In progress",
            orderDate: "2024-04-01",
        },
        // ... Add remaining table data
    ];
    //const [isLoading, setLoading] = useState(true)

    /*useEffect(() => {
        getStatisticsRequest()
            .then((data) => {
                setStatisticsData(data)
                setLoading(false)
            })

    }, []);*/

    return (
        <div className={style.container}>
            <table className={style.table}>
                <thead>
                <tr>
                    <th>Registration ID</th>
                    <th>User Name</th>
                    <th>Institution</th>
                    <th>Service</th>
                    <th>Patient(s) Name</th>
                    <th>MRN</th>
                    <th>Patient BOD</th>
                    <th>Current Status</th>
                    <th>Order Date</th>
                </tr>
                </thead>
                <tbody>
                {tableData.map((row) => (
                    <tr key={row.registrationId}>
                        <td>{row.registrationId}</td>
                        <td>{row.userName}</td>
                        <td>{row.institution}</td>
                        <td>{row.service}</td>
                        <td>{row.patientName}</td>
                        <td>{row.mrn}</td>
                        <td>{row.patientBod}</td>
                        <td>{row.currentStatus}</td>
                        <td>{row.orderDate}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}