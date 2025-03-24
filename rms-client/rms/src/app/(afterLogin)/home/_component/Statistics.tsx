"use client"

import style from "@/app/(afterLogin)/home/_component/statistics.module.css";
import {useEffect, useState} from "react";
import type {Statistics} from "@/model/Statistics";
import {getStatisticsRequest} from "@/app/(afterLogin)/home/_api/getStatisticsRequest";
import Loading from "@/app/(afterLogin)/_component/Loading";
import Link from "next/link";
import {Status} from "@/model/Status";
import {useSetStatus} from "@/app/(afterLogin)/request/dashboard/store/useStatusStore";


export default function Statistics() {
    const [statisticsData, setStatisticsData] = useState<Statistics | undefined>()
    const setStatus = useSetStatus();

    const handleCardOnClick = (status: Status) => {
        setStatus(status);
    };

    useEffect(() => {
        const fetchData = async () => {
            const response = await getStatisticsRequest();
            if (!response.ok) {
                setStatisticsData(undefined);
            } else {
                const data = await response.json();
                setStatisticsData(data as Statistics);
            }
        };
        fetchData()
    }, []);

    const statisticsCards: {
        label: string; value?: number;
        status: Status
    }[] = [
        { label: "Total", value: statisticsData?.total, status: Status.TOTAL },
        { label: "Pending Approval", value: statisticsData?.unconfirmed_order, status: Status.UNCONFIRMED_ORDER },
        { label: "Approval", value: statisticsData?.completed_order, status: Status.COMPLETED_ORDER },
        { label: "In progress", value: statisticsData?.in_progress, status: Status.IN_PROGRESS },
        { label: "Test failed", value: statisticsData?.test_failed, status: Status.TEST_FAILED },
        { label: "Delivered", value: statisticsData?.delivered, status: Status.DELIVERED },
        { label: "Completed", value: statisticsData?.completed, status: Status.COMPLETED },
    ];

    return (
        <div className={style.container}>
            <div className={style.cardContainer}>
                {statisticsCards.map((card) => (
                    <Link href={"/request/dashboard"} className={style.card} key={card.label}
                          onClick={() => handleCardOnClick(card.status)}>
                        <div className={style.cardLabel}>{card.label}</div>
                        <div className={style.cardValue}>
                            {card.value !== undefined ? card.value : <Loading/>}
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    )
}