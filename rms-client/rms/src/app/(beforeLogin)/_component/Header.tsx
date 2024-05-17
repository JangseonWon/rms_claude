"use client"

import style from "@/app/(beforeLogin)/_component/header.module.css"
import Link from "next/link";
import {useRouter, useSelectedLayoutSegment} from "next/navigation";


export default function Header() {
    const segment = useSelectedLayoutSegment();
    const router = useRouter();
    const buttonText = segment === 'help' ? 'Go to Login' : 'Need help?';

    const helpClassName = segment === 'help' ? `${style.needHelp} ${style.needHelpActive}` : style.needHelp;
    const handleNeedHelpClick = () => {
        if (segment === 'help') {
            router.push('/login');
        } else {
            router.push('/help');
        }
    };
    return (
        <header className={style.header}>
            <div>
                <Link href={"/login"} className={style.gPortalLogo}>G-Portal</Link>
                <Link href={"https://oversea.gcgenome.com/ "} className={style.headerMenu}>Home</Link>
                <Link href={"https://www.linkedin.com/company/73449146/admin/feed/posts"} className={style.headerMenu}>LINKED IN</Link>
            </div>
            <div>
                <button className={helpClassName} onClick={handleNeedHelpClick}>
                    {buttonText}
                </button>
            </div>
        </header>
    )
}