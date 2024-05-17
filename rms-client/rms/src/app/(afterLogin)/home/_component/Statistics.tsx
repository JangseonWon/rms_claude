"use client"

import style from "@/app/(afterLogin)/home/_component/statistics.module.css";
import {useEffect, useState} from "react";
import type {Statistics} from "@/model/Statistics";
import {getStatisticsRequest} from "@/app/(afterLogin)/home/_api/getStatisticsRequest";
import Loading from "@/app/(afterLogin)/_component/Loading";
import Link from "next/link";


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
            <div className={style.line}></div>
            <h1>Need more help?
                <span>
                    <Link href={"/qna"}>
                    <button>Go to QnA</button>
                    </Link>
                </span>
            </h1>
            <div className={style.cardContainer}>
                {statisticsCards.map((card) => (
                    <Link href={"/request/order"} className={style.card}>
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