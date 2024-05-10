"use client"

import style from "@/app/(afterLogin)/request/dashboard/dashboard/_component/statistics.module.css"
import {useRequestStore} from "@/store/organization";
import {useEffect, useState} from "react";
import {getStatisticsRequest} from "@/app/(afterLogin)/request/dashboard/dashboard/_api/getStatisticsRequest";
import type {Statistics} from "@/model/Statistics";
import Loading from "@/app/(afterLogin)/_component/Loading";

export default function Statistics() {
    const [statisticsData, setStatisticsData] = useState<Statistics>()

    useEffect(() => {
        getStatisticsRequest()
            .then((data) => {
                setStatisticsData(data)
            })

    }, []);

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
                <div className={style.card}>
                    <div className={style.cardLabel}>Total</div>
                    <div className={style.cardValue}>{statisticsData ? (
                        statisticsData.total
                    ) : <Loading/>}</div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>Ordered</div>
                    <div className={style.cardValue}>
                        {statisticsData ? (
                            statisticsData.ordered
                        ) : <Loading/>}
                    </div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>In progress</div>
                    <div className={style.cardValue}>
                        {statisticsData ? (
                            statisticsData.inProgress
                        ) : <Loading/>}
                    </div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>Test failed</div>
                    <div className={style.cardValue}>
                        {statisticsData ? (
                            statisticsData.testFailed
                        ) : <Loading/>}
                    </div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>Delivered</div>
                    <div className={style.cardValue}>
                        {statisticsData ? (
                            statisticsData.delivered
                        ) : <Loading/>}
                    </div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>Complete</div>
                    <div className={style.cardValue}>
                        {statisticsData ? (
                            statisticsData.finished
                        ) : <Loading/>}
                    </div>
                </div>
            </div>
        </div>
    )
}