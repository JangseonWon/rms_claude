"use client"

import style from "@/app/(afterLogin)/request/cart/_component/title.module.css"

export default function Title() {
    return (
        <div className={style.container}>
            <div className={style.subTitle}>
                Cart
            </div>
            <div className={style.mainTitle}>
                Cart
            </div>
        </div>
    )
}