'use client';

import style from "./singleMultiChangeButton.module.css";
import React from "react";
import {usePathname, useRouter} from "next/navigation";

export default function SingleMultiChangeButton() {
    const router = useRouter();
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const lastValue = decodeURIComponent(pathSegments.pop() || '');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 1]);
    const order = lastValue === 'single' ? 'multi' : 'single';

    const excelRequest = () => {
        router.push(`/request/services/${serviceId}/${order}`);
    };

    const buttonText = order === 'single' ? 'Single-case' : 'Multi-cases';

    return (
        <button className={style.changeButton} onClick={excelRequest}>
            {buttonText}
        </button>
    );
}