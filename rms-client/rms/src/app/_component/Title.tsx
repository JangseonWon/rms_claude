"use client"

import style from "@/app/_component/title.module.css"
import {usePathname} from "next/navigation";

export default function Title() {
    const pathname = usePathname();
    const lastValue = pathname.split('/').pop();

    let correctedValue;

    switch (lastValue) {
        case 'precision-oncology':
            correctedValue = 'Precision Oncology'
            break;
        case 'pre-and-neonatal':
            correctedValue = 'Pre & neonatal';
            break;
        case 'rare-disease':
            correctedValue = 'Rare disease'
            break;
        case 'health-checkup':
            correctedValue = 'Health Checkup'
            break;
        case 'download':
            correctedValue = 'Download'
            break;
        case 'resample':
            correctedValue = 'Re-sample'
            break;
        case 'others':
            correctedValue = 'Others'
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