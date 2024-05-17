"use client"

import style from "@/app/(afterLogin)/request/dashboard/dashboard/_component/statistics.module.css"
import {useEffect, useState} from "react";
import {getStatisticsRequest} from "@/app/(afterLogin)/request/dashboard/dashboard/_api/getStatisticsRequest";
import type {Statistics} from "@/model/Statistics";
import Loading from "@/app/(afterLogin)/_component/Loading";

export default function Statistics() {
    const [statisticsData, setStatisticsData] = useState<Statistics>()

    useEffect(() => {
        const fetchData = async () => {
            const response = await getStatisticsRequest();
            const data = await response.json();
            setStatisticsData(data as Statistics);
        };
        fetchData()
    }, []);

    const statisticsCards = [
        { label: "Total", value: statisticsData?.total },
        { label: "Ordered", value: statisticsData?.ordered },
        { label: "In progress", value: statisticsData?.in_progress },
        { label: "Test failed", value: statisticsData?.test_failed },
        { label: "Delivered", value: statisticsData?.delivered },
        { label: "Complete", value: statisticsData?.finished },
    ];

    return (
        <div className={style.container}>
            <div className={style.titleContainer}>
                <div className={style.subTitle}>
                    Dashboard &gt; <span>Dashboard</span>
                </div>
                <div className={style.mainTitle}>
                    Dashboard
                </div>
            </div>
            <div className={style.cardContainer}>
                {statisticsCards.map((card) => (
                    <div className={style.card} key={card.label}>
                        <div className={style.cardLabel}>{card.label}</div>
                        <div className={style.cardValue}>
                            {card.value !== undefined ? card.value : <Loading/>}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}