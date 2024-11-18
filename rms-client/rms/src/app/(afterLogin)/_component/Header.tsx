"use client"

import style from "@/app/(afterLogin)/_component/header.module.css"
import ProfileButton from "@/app/(afterLogin)/_component/ProfileButton";
import Link from "next/link";

export default function Header() {
    return (
        <div className={style.header}>
            <div className={style.headerWidth}>
                <div className={style.leftContainer}>
                    <Link href={"/home"} className={style.gPortalLogo}>G-Portal</Link>
                </div>
                <div className={style.rightContainer}>
                    <Link href={"/qna"}
                          className={style.needHelp}>Need Help?</Link>
                    <ProfileButton/>
                </div>
            </div>
        </div>
    )
}