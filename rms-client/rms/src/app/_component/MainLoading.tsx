"use client"

import Image from "next/image";
import mainImg from "@/../public/gc-genome-logo.png";
import style from './mainLoading.module.css';


export default function MainLoading() {
    return (
        <div className={style.container}>
            <div className={style.loadingImg}>
                <Image src={mainImg} alt={"loadingImg"} width={800}/>
            </div>
            <div className={style.ballContainer}>
                <div className={style.ball}></div>
                <div className={style.ball}></div>
                <div className={style.ball}></div>
                <div className={style.ball}></div>
                <div className={style.ball}></div>
            </div>
            <span className={style.title}>G-portal</span>
            <span className={style.loadingText}>is loading</span>
        </div>
    )
}