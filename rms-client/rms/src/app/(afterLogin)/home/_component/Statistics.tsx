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
        getStatisticsRequest()
            .then((data) => {
                setStatisticsData(data)
            })

    }, []);

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
                <Link href={"/request/order"} className={style.card}>
                    <div className={style.cardLabel}>Total</div>
                    <div className={style.cardValue}>{statisticsData ? (
                        statisticsData.total
                    ): <Loading/>}</div>
                </Link>
                <Link href={"/request/order"} className={style.card}>
                    <div className={style.cardLabel}>Ordered</div>
                    <div className={style.cardValue}>
                        {statisticsData ? (
                            statisticsData.ordered
                        ): <Loading/>}
                    </div>
                </Link>
                <Link href={"/request/order"} className={style.card}>
                        <div className={style.cardLabel}>In progress</div>
                        <div className={style.cardValue}>
                            {statisticsData ? (
                                statisticsData.inProgress
                            ): <Loading/>}
                        </div>
                </Link>
                <Link href={"/request/order"} className={style.card}>
                        <div className={style.cardLabel}>Test failed</div>
                        <div className={style.cardValue}>
                            {statisticsData ? (
                                statisticsData.testFailed
                            ): <Loading/>}
                        </div>
                </Link>
                <Link href={"/request/order"} className={style.card}>

                        <div className={style.cardLabel}>Delivered</div>
                        <div className={style.cardValue}>
                            {statisticsData ? (
                                statisticsData.delivered
                            ): <Loading/>}
                        </div>
                </Link>
                <Link href={"/request/order"} className={style.card}>
                    <div className={style.cardLabel}>Complete</div>
                    <div className={style.cardValue}>
                        {statisticsData ? (
                            statisticsData.finished
                        ): <Loading/>}
                    </div>
                </Link>
            </div>
        </div>
    )
}