"use client";

import style from "./profileAlarm.module.css";
import scroll from "@/css/scrollBar.module.css";
import React, {useEffect, useState} from "react";
import classNames from "classnames";
import {Alarm} from "@/model/Alarm";
import {getAlarmByUser} from "@/app/(afterLogin)/_component/alarm/_api/getAlarmByUser";
import {useRouter} from "next/navigation";


export default function ProfileAlarm() {
    const [alarmData, setAlarmData] = useState<Alarm[]>();
    const route = useRouter();

    useEffect(() => {
        const fetchData = async () => {
            const response = await getAlarmByUser();
            if (!response.ok) {
                setAlarmData([]);
            } else {
                const data = await response.json();
                setAlarmData(data as Alarm[]);
            }
        };
        fetchData()
    }, []);

    function isNew(lastModifyDate: string): boolean {
        const now = new Date();
        const lastModified = new Date(lastModifyDate);
        const diffInMilliseconds = now.getTime() - lastModified.getTime();
        const diffInHours = Math.floor(diffInMilliseconds / (1000 * 3600));

        return diffInHours <= 24;
    }

    function formatDateDifference(lastModifyDate: string): string {
        const now = new Date();
        const lastModified = new Date(lastModifyDate);
        const diffInMilliseconds = now.getTime() - lastModified.getTime();
        const diffInMinutes = Math.floor(diffInMilliseconds / (1000 * 60));
        const diffInHours = Math.floor(diffInMilliseconds / (1000 * 3600));
        const diffInDays = Math.floor(diffInMilliseconds / (1000 * 3600 * 24));
        const diffInMonths = Math.floor(diffInDays / 30);
        const diffInYears = Math.floor(diffInDays / 365);

        if (diffInMinutes < 60) {
            return diffInMinutes === 1 ? '1 minute ago' : `${diffInMinutes} minutes ago`;
        }

        if (diffInHours < 24) {
            return diffInHours === 1 ? '1 hour ago' : `${diffInHours} hours ago`;
        }

        if (diffInDays <= 30) {
            return diffInDays === 1 ? '1 day ago' : `${diffInDays} days ago`;
        }

        if (diffInMonths <= 12) {
            return diffInMonths === 1 ? '1 month ago' : `${diffInMonths} months ago`;
        }

        return diffInYears === 1 ? '1 year ago' : `${diffInYears} years ago`;
    }

    function transformCategoryName(category: string) {
        return category
            .split('_')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ');
    }

    return(
        <div className={style.container}>
            <section className={classNames(style.contentContainer, scroll.default)}>
                {alarmData && alarmData.length > 0 ? ( alarmData.map((row, index) => {
                    const link =
                        row.category === "qna"
                            ? `/qna/${row.post_id}`
                            : `/qna/${row.category}/${row.post_id}`;

                    return (
                        <div key={index} className={style.content} onClick={() => route.push(link)}>
                            <div className={style.titleWrapper}>
                                <div className={style.titleContainer}>
                                <span className={style.title}>
                                    {transformCategoryName(row.category!)}
                                </span>
                                    {isNew(row.last_modify_at!) && <div className={style.new}>New</div>}
                                </div>
                                <div className={style.titleContent}>
                                    {row.content}
                                </div>
                            </div>
                            <div className={style.date}>
                                {formatDateDifference(row.last_modify_at!)}
                            </div>
                        </div>
                    );
                })
                ) : (
                    <div className={style.title}>
                        No Alarm
                    </div>
                )}
            </section>
        </div>
    )
}