"use client"

import style from "@/app/(afterLogin)/home/_component/testOption.module.css"
import Link from "next/link";
import Image from "next/image";
import homeMainImg from "@/../public/home_main.jpg"

export default function TestOption() {
    return (
        <div className={style.container}>
            <div className={style.line}></div>
            <h1>Your Testing Options</h1>
            <p>Choose one of available segments to start an order using G-Portal</p>
            <div className={style.cardContainer}>
                <Link href={"/request/services/precision-oncology"}>
                    <div className={style.card}>
                            <Image src={homeMainImg} alt={"Precision Oncology"}/>
                            <div className={style.cardLabel}>Precision Oncology</div>
                    </div>
                </Link>
                <Link href={"/request/services/pre-and-neonatal"}>
                    <div className={style.card}>
                            <Image src={homeMainImg} alt={">Pre & neonatal"}/>
                            <div className={style.cardLabel}>Pre & neonatal</div>
                    </div>
                </Link>
                <Link href={"/request/services/rare-disease"}>
                    <div className={style.card}>
                            <Image src={homeMainImg} alt={"Rare disease"}/>
                            <div className={style.cardLabel}>Rare disease</div>
                    </div>
                </Link>
                <Link href={"/request/services/health-checkup"}>
                    <div className={style.card}>
                        <Image src={homeMainImg} alt={"Health Checkup & Others"}/>
                        <div className={style.cardLabel}>Health Checkup & Others</div>
                    </div>
                </Link>
            </div>
        </div>
    )
}