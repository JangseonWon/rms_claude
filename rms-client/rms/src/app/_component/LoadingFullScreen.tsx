"use client"

import Image from "next/image";
import loadingImg from "@/../public/loading.gif";
import style from "./loadingFullScreen.module.css";


export default function LoadingFullScreen() {
    return (
        <div className={style.loading}>
            <Image src={loadingImg} alt={"loading"}/>
        </div>
    )
}