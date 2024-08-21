"use client"

import style from "@/app/_component/title.module.css"
import {usePathname} from "next/navigation";

export default function Title() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const capitalize = (str: string) => {
        return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
    };

    const lastValue = capitalize(decodeURIComponent(pathSegments.pop() || ''));
    const secondLastValue = capitalize(decodeURIComponent(pathSegments.pop() || ''));

    return (
        <div className={style.title}>
            <div className={style.subTitle}>
                {secondLastValue} &gt; <span>{lastValue}</span>
            </div>
            <div className={style.mainTitle}>
                {lastValue}
            </div>
        </div>
    )
}