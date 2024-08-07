"use client"

import Image from "next/image";
import loadingImg from "@/../public/loading.gif";
import style from "./qnaLoading.module.css";


export default function QnaLoading() {
    return (
        <div className={style.loading}>
            <Image src={loadingImg} alt={"loading"}/>
        </div>
    )
}