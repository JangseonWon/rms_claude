"use client"

import style from "@/app/_component/title.module.css"
import {usePathname} from "next/navigation";

export default function Title() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const lastValue = decodeURIComponent(pathSegments.pop() || '');
    const secondLastValue = decodeURIComponent(pathSegments.pop() || '');

    let correctedValue;

    switch (lastValue) {
        case 'single':
        case 'multi':
            correctedValue = secondLastValue
            break;
        default:
            correctedValue = lastValue;
            break;
    }

    return (
        <div className={style.title}>
            <div className={style.subTitle}>
                Service &gt; <span>{correctedValue}</span>
            </div>
            <div className={style.mainTitle}>
                {correctedValue}
            </div>
        </div>
    )
}