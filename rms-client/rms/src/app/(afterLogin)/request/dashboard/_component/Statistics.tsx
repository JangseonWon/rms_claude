"use client"

import style from "@/app/(afterLogin)/request/dashboard/_component/statistics.module.css"
import {useEffect, useState} from "react";
import {getStatisticsRequest} from "@/app/(afterLogin)/request/dashboard/_api/getStatisticsRequest";
import type {Statistics} from "@/model/Statistics";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {useSetStatus} from "@/app/(afterLogin)/request/dashboard/store/useStatusStore";
import {Status} from "@/model/Status";

export default function Statistics() {
    const [statisticsData, setStatisticsData] = useState<Statistics>()
    const setStatus = useSetStatus();

    useEffect(() => {
        const fetchData = async () => {
            const response = await getStatisticsRequest();
            const data = await response.json();
            setStatisticsData(data as Statistics);
        };
        fetchData()
    }, []);

    const handleCardOnClick = (status: Status) => {
        setStatus(status);
    };

    const statisticsCards: {
        label: string; value?: number;
        status: Status
    }[] = [
        { label: "Total", value: statisticsData?.total, status: Status.TOTAL },
        { label: "Ordered", value: statisticsData?.ordered, status: Status.ORDERED },
        { label: "In progress", value: statisticsData?.in_progress, status: Status.INPROGRESS },
        { label: "Test failed", value: statisticsData?.test_failed, status: Status.TESTFAILED },
        { label: "Delivered", value: statisticsData?.delivered, status: Status.DELIVERED },
        { label: "Complete", value: statisticsData?.finished, status: Status.COMPLETE },
    ];

    return (
        <div className={style.container}>
            <div className={style.titleContainer}>
                <div className={style.mainTitle}>
                    Dashboard
                </div>
            </div>
            <div className={style.cardContainer}>
                {statisticsCards.map((card) => (
                    <div className={style.card}
                         key={card.label}
                         onClick={() => handleCardOnClick(card.status)}
                    >
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