"use client"

import style from "@/app/(afterLogin)/request/dashboard/dashboard/_component/statistics.module.css"
import {useRequestStore} from "@/store/organization";
import {useEffect, useState} from "react";
import {getStatisticsRequest} from "@/app/(afterLogin)/request/dashboard/dashboard/_api/getStatisticsRequest";
import type {Statistics} from "@/model/Statistics";
import Loading from "@/app/(afterLogin)/_component/Loading";

export default function Statistics() {
    const [statisticsData, setStatisticsData] = useState<Statistics>()
    const [isLoading, setLoading] = useState(true)

    useEffect(() => {
        getStatisticsRequest()
            .then((data) => {
                setStatisticsData(data)
                setLoading(false)
            })

    }, []);

    if(isLoading) {
        return(
            <div className={style.container} style={{height:"240px"}}>
                <Loading/>
            </div>
        )
    }

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
                    <div className={style.cardValue}>{statisticsData?.total}</div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>Ordered</div>
                    <div className={style.cardValue}>{statisticsData?.ordered}</div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>In progress</div>
                    <div className={style.cardValue}>{statisticsData?.inProgress}</div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>Test failed</div>
                    <div className={style.cardValue}>{statisticsData?.testFailed}</div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>Delivered</div>
                    <div className={style.cardValue}>{statisticsData?.delivered}</div>
                </div>
                <div className={style.card}>
                    <div className={style.cardLabel}>Complete</div>
                    <div className={style.cardValue}>{statisticsData?.finished}</div>
                </div>
            </div>
        </div>
    )
}