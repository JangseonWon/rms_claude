"use client"

import Image from "next/image";
import style from "@/app/(afterLogin)/_component/loading.module.css"
import loadingImg from "@/../public/loading.gif"


export default function Loading() {
    return <Image src={loadingImg} alt={"loading"} className={style.loading}/>
}