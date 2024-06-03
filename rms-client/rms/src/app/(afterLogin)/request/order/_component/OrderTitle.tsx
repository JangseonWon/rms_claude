"use client"

import style from "@/app/(afterLogin)/request/order/_component/orderTitle.module.css";

export default function OrderTitle() {
    return (
        <div className={style.container}>
            <div className={style.subTitle}>
                Request Order
            </div>
            <div className={style.mainTitle}>
                Request Order
            </div>
        </div>
    )
}