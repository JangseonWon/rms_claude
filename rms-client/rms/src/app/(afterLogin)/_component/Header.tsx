"use client"

import style from "@/app/(afterLogin)/_component/header.module.css"
import {Session} from "@auth/core/types";
import ProfileButton from "@/app/(afterLogin)/_component/ProfileButton";
import Link from "next/link";
import {useSelectedLayoutSegment} from "next/navigation";

type Props = {
    session: Session | null
}

export default function Header({session}: Props) {
    const segment = useSelectedLayoutSegment();
    return (
        <header className={style.header}>
            <div>
                <Link href={"/home"} className={style.gPortalLogo}>G-Portal</Link>
                <Link href={"/request/dashboard/dashboard"} className={segment?.includes('request') ? style.headerMenuActive : style.headerMenu }>Request order</Link>
                <Link href={"/request/qna"} className={segment?.includes('qna') ? style.headerMenuActive : style.headerMenu }>QnA</Link>
            </div>
            <ProfileButton session={session}/>
        </header>
    )
}