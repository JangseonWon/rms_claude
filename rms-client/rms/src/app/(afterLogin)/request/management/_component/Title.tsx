"use client"

import style from "@/app/(afterLogin)/request/management/_component/title.module.css"
import {usePathname} from "next/navigation";

export default function Title() {
    const pathname = usePathname();
    const lastValue = pathname.split('/').pop();

    let correctedValue;

    switch (lastValue) {
        case 'category':
            correctedValue = 'Category'
            break;
        case 'institution':
            correctedValue = 'Institution';
            break;
        case 'service':
            correctedValue = 'Service'
            break;
        case 'user':
            correctedValue = 'User'
            break;
        default:
            correctedValue = lastValue;
            break;
    }

    return (
        <div className={style.mainTitle}>
            {correctedValue} Management
        </div>
    )
}