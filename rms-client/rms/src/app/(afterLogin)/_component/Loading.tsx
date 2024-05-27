"use client"

import Image from "next/image";
import loadingImg from "@/../public/loading.gif"
import style from "@/app/(afterLogin)/_component/loading.module.css"


export default function Loading() {
    return (
        <div className={style.loading}>
            <Image src={loadingImg} alt={"loading"}/>
        </div>
    )
}