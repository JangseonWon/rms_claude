"use client"

import style from "@/app/(afterLogin)/_component/header.module.css"
import ProfileButton from "@/app/(afterLogin)/_component/ProfileButton";
import Link from "next/link";
import {useSelectedLayoutSegment} from "next/navigation";

export default function Header() {
    const segment = useSelectedLayoutSegment();
    return (
        <div className={style.header}>
            <div className={style.headerWidth}>
                <div className={style.leftContainer}>
                    <Link href={"/home"} className={style.gPortalLogo}>G-Portal</Link>
                </div>
                <div className={style.rightContainer}>
                    <Link href={"/qna"}
                          className={segment?.includes('qna') ? style.headerMenuActive : style.headerMenu}>QnA</Link>
                    <ProfileButton/>
                </div>
            </div>
        </div>
    )
}