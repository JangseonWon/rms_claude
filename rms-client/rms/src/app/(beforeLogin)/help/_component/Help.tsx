"use client"

import React from "react";
import style from "@/app/(beforeLogin)/help/_component/help.module.css"
import Image from "next/image";
import loginImg from "@/../public/login-img2.png"

export default function Help() {
    return (
        <div className={style.container}>
            <div className={style.left}>
                <Image src={loginImg} alt="img"/>
            </div>
            <div className={style.right}>
                <div className={style.labelContainer1}>
                    <div className={style.line}/>
                    <label className={style.mainLabel1}>Get in Touch<br/>With Our Support</label>
                    <label className={style.subLabel1}>For general inquiry & account access problem</label>
                    <label className={style.mainLabel2}>+82 (0)31 280 9910</label>
                    <label className={style.subLabel2}>Mon - Fri a.m - 6:30 p.m KRT <br/> Sat 9 a.m - 12 p.m</label>
                    <a href="mailto:info@gcgenome.com" className={style.contactUs1}>
                        Contact Us
                    </a>
                </div>
                <div className={style.labelContainer2}>
                    <label className={style.subLabel1}>Result counseling</label>
                    <label className={style.mainLabel2}>info@gcgenome.com</label>
                    <label className={style.subLabel2}>Mon -Fri 9 a.m - 5:30 p.m KRT</label>
                    <a href="mailto:info@gcgenome.com" className={style.contactUs2}>
                        Contact Us
                    </a>
                </div>
            </div>
        </div>
    )
}