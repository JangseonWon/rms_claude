"use client"

import style from "@/app/(afterLogin)/home/_component/orderBoard.module.css";
import scrollbar from "@/css/scrollBar.module.css";
import React, {useEffect, useState} from "react";
import type {Statistics} from "@/model/Statistics";
import {getStatisticsRequest} from "@/app/(afterLogin)/home/_api/getStatisticsRequest";
import Loading from "@/app/(afterLogin)/_component/Loading";
import classNames from "classnames";


export default function OrderBoard() {
    const [statisticsData, setStatisticsData] = useState<Statistics>();

    const statistics = [
        { label: "Unconfirmed Order", value: statisticsData?.unconfirmed_order },
        { label: "Completed Order", value: statisticsData?.completed_order },
        { label: "In progress", value: statisticsData?.in_progress },
        { label: "Test failed", value: statisticsData?.test_failed },
        { label: "Delivered", value: statisticsData?.delivered },
    ];

    const requests = [
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광일', status: 'unconfirmed_order', new: true },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광이', status: 'unconfirmed_order', new: true },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광삼', status: 'unconfirmed_order', new: true },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광사', status: 'unconfirmed_order', new: true },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광오', status: 'unconfirmed_order', new: true },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광육', status: 'unconfirmed_order', new: true },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광칠', status: 'unconfirmed_order', new: true },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광팔', status: 'unconfirmed_order', new: false },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광구', status: 'unconfirmed_order', new: false },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신광십', status: 'unconfirmed_order', new: false },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: '신십일', status: 'unconfirmed_order', new: false },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: 'Shin GwangWoong', status: 'unconfirmed_order', new: false },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: 'Shin GwangWoong', status: 'unconfirmed_order', new: false },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: 'Shin GwangWoong', status: 'unconfirmed_order', new: false },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: 'Shin GwangWoong', status: 'unconfirmed_order', new: false },
        {organization: '기관명',patient: 'gwang', date: '2024-10-11', name: 'Shin GwangWoong', status: 'unconfirmed_order', new: false },
    ];

    useEffect(() => {
        const fetchData = async () => {
            const response = await getStatisticsRequest();
            const data = await response.json();
            setStatisticsData(data as Statistics);
        };
        fetchData()
    }, []);


    return (
        <div className={style.container}>
            <div className={style.title}>
                <div className={style.line}></div>
                <h1>Notice Board</h1>
            </div>
            <div className={style.orderContainer}>
            <div className={style.orderCountContainer}>
                    {statistics.map((order) => (
                        <div className={style.orderCount}>
                            <div className={style.orderLabel}>{order.label}</div>
                            <div className={style.orderValue}>
                                {order.value !== undefined ? order.value : <Loading/>}
                            </div>
                        </div>
                    ))}
                </div>
                <div className={style.alarmContainer}>
                    <div className={classNames(style.wrapper, scrollbar.default)}>
                        <span className={style.boardTitle}>Please Enter Global courier & AirWaybill No</span>
                        {requests.map((request) => (
                            <div className={style.alarm}>
                                <div>Date: {request.date}</div>
                                <div>Patient Name: {request.patient}</div>
                                <div>Client: {request.name}</div>
                                <div>Institution: {request.organization}</div>
                                <div>{request.new}</div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}