"use client"

import style from "@/app/(afterLogin)/request/dashboard/_component/statistics.module.css"
import {useEffect, useState} from "react";
import {getStatisticsRequest} from "@/app/(afterLogin)/request/dashboard/_api/getStatisticsRequest";
import type {Statistics} from "@/model/Statistics";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {useSetStatus} from "@/app/(afterLogin)/request/dashboard/store/useStatusStore";
import {Status} from "@/model/Status";
import {useRouter} from "next/navigation";

export default function Statistics() {
    const [statisticsData, setStatisticsData] = useState<Statistics>()
    const setStatus = useSetStatus();
    const [activeStatus, setActiveStatus] = useState<Status | null>(null);
    // const router = useRouter();

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
        setActiveStatus(status);
    };

    const statisticsCards: {
        label: string; value?: number;
        status: Status
    }[] = [
        { label: "Total", value: statisticsData?.total, status: Status.TOTAL },
        { label: "Unconfirmed Order", value: statisticsData?.unconfirmed_order, status: Status.UNCONFIRMED_ORDER },
        { label: "Completed Order", value: statisticsData?.completed_order, status: Status.COMPLETED_ORDER },
        { label: "In progress", value: statisticsData?.in_progress, status: Status.IN_PROGRESS },
        { label: "Test failed", value: statisticsData?.test_failed, status: Status.TEST_FAILED },
        { label: "Delivered", value: statisticsData?.delivered, status: Status.DELIVERED },
        { label: "Completed", value: statisticsData?.completed, status: Status.COMPLETED },
    ];

    const handleLearnMoreClick = (status: Status) => {
        // router.push(`/dashboard/${status}`);
        alert(`${status} Learn More`);
    };

    return (
        <div className={style.container}>
            <div className={style.titleContainer}>
                <div className={style.mainTitle}>
                    Dashboard
                </div>
            </div>
            <div className={style.cardContainer}>
                {statisticsCards.map((card) => (
                    <div
                        className={`${style.card} ${
                            activeStatus === card.status ? style.activeCard : ""
                        }`}
                         key={card.label}
                         onClick={() => handleCardOnClick(card.status)}
                    >
                        <div className={style.cardLabel}>{card.label}</div>
                        <div className={style.cardValue}>
                            {card.value !== undefined ? card.value : <Loading/>}
                        </div>
                        <div
                            className={style.learnMoreBox}
                            onClick={(e) => {
                                e.stopPropagation();
                                handleLearnMoreClick(card.status);
                            }}
                        >
                            <span className={style.learnMore}>Learn more</span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}