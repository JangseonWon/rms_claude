"use client"

import style from "@/app/_component/title.module.css"
import {usePathname} from "next/navigation";

export default function Title() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const lastValue = decodeURIComponent(pathSegments.pop() || '');

    return (
        <div className={style.title}>
            <div className={style.mainTitle}>
                {lastValue}
            </div>
        </div>
    )
}