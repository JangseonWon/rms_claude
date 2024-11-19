"use client"

import style from "./profileAlarm.module.css";
import scroll from "@/css/scrollBar.module.css";
import React from "react";
import classNames from "classnames";

export default function ProfileAlarm() {
    const alarmData = {
        "total_count": 10,
        "notice_count": 3,
        "unconfirmed_count":4,
        "completed_count":3,
        "data": [
            {
                "category": "notice",
                "title": "test Notice 1",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "notice",
                "title": "test Notice 2",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "notice",
                "title": "test Notice 3",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "unconfirmed_order",
                "title": "test Unconfirmed Order 1",
                "create_at": "2024-11-29 11:48:09.694172",
                "last_modify_at": "2024-11-30 11:48:09.694172"
            },
            {
                "category": "unconfirmed_order",
                "title": "test Unconfirmed Order 2",
                "create_at": "2024-11-29 11:48:09.694172",
                "last_modify_at": "2024-11-30 11:48:09.694172"
            },
            {
                "category": "unconfirmed_order",
                "title": "test Unconfirmed Order 3",
                "create_at": "2024-11-29 11:48:09.694172",
                "last_modify_at": "2024-11-30 11:48:09.694172"
            },
            {
                "category": "unconfirmed_order",
                "title": "test Unconfirmed Order 4",
                "create_at": "2024-11-29 11:48:09.694172",
                "last_modify_at": "2024-11-30 11:48:09.694172"
            },
            {
                "category": "completed_order",
                "title": "test Completed Order 1",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "completed_order",
                "title": "test Completed Order 2",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "completed_order",
                "title": "test Completed Order 3",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "notice",
                "title": "test Notice 1",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "notice",
                "title": "test Notice 2",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "notice",
                "title": "test Notice 3",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "unconfirmed_order",
                "title": "test Unconfirmed Order 1",
                "create_at": "2024-11-29 11:48:09.694172",
                "last_modify_at": "2024-11-30 11:48:09.694172"
            },
            {
                "category": "unconfirmed_order",
                "title": "test Unconfirmed Order 2",
                "create_at": "2024-11-29 11:48:09.694172",
                "last_modify_at": "2024-11-30 11:48:09.694172"
            },
            {
                "category": "unconfirmed_order",
                "title": "test Unconfirmed Order 3",
                "create_at": "2024-11-29 11:48:09.694172",
                "last_modify_at": "2024-11-30 11:48:09.694172"
            },
            {
                "category": "unconfirmed_order",
                "title": "test Unconfirmed Order 4",
                "create_at": "2024-11-29 11:48:09.694172",
                "last_modify_at": "2024-11-30 11:48:09.694172"
            },
            {
                "category": "completed_order",
                "title": "test Completed Order 1",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "completed_order",
                "title": "test Completed Order 2",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            },
            {
                "category": "completed_order",
                "title": "test Completed Order 3",
                "create_at": "2024-05-30 11:48:09.694172",
                "last_modify_at": "2024-07-30 11:48:09.694172"
            }
        ]
    }

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
        const diffInDays = Math.floor(diffInMilliseconds / (1000 * 3600 * 24));
        const diffInMonths = Math.floor(diffInDays / 30);
        const diffInYears = Math.floor(diffInDays / 365);

        if (diffInDays <= 30) {
            if (diffInDays === 1) {
                return '1 day ago';
            }
            return `${diffInDays} days ago`;
        }

        if (diffInMonths <= 12) {
            if (diffInMonths === 1) {
                return '1 month ago';
            }
            return `${diffInMonths} months ago`;
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
            <section className={style.menuContainer}>
                <div className={style.menu}>
                    All
                </div>
                <div className={style.menu}>
                    Notice
                </div>
                <div className={style.menu}>
                    <span>Unconfirmed</span>
                    <span>Order</span>
                </div>
                <div className={style.menu}>
                    <span>Completed</span>
                    <span>Order</span>
                </div>
            </section>
            <section className={classNames(style.contentContainer, scroll.default)}>
                {alarmData && alarmData.data.length > 0 && alarmData.data.map((row, index) => (
                    <div key={index} className={style.content}>
                        <div className={style.titleWrapper}>
                            <div className={style.titleContainer}>
                                <span className={style.title}>
                                    {transformCategoryName(row.category)}
                                </span>
                                {isNew(row.last_modify_at) && <div className={style.new}>New</div>}
                            </div>
                            <div className={style.titleContent}>
                                {row.title}
                            </div>
                        </div>
                        <div className={style.date}>
                            {formatDateDifference(row.last_modify_at)}
                        </div>
                    </div>
                ))}
            </section>
        </div>
    )
}