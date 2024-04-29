"use client"

import style from "@/app/(afterLogin)/request/_component/navMenu.module.css"
import Link from "next/link";
import {faHouse, faAngleDown, faGripLines, faBorderAll, faPlus, faCircleCheck, faCircleQuestion} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";
import {useSelectedLayoutSegment} from "next/navigation";


export default function NavMenu() {
    const segment = useSelectedLayoutSegment();

    return (
        <div className={style.navPill}>
            <ul>
                <li>
                    <Link href={"/request/dashboard"}>
                        <FontAwesomeIcon
                            className={style.icon}
                            style={{color: segment?.includes('dashboard') ? '#90BA2D' : '#666666'}}
                            icon={faHouse}/>
                        <span>Dashboard</span>
                        <FontAwesomeIcon icon={faAngleDown}/>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/services"}>
                        <FontAwesomeIcon
                            className={style.icon}
                            style={{color: segment?.includes('services') ? '#90BA2D' : '#666666'}}
                            icon={faBorderAll}/>
                        <span>Services</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/cart"}>
                        <FontAwesomeIcon
                            className={style.icon}
                            style={{color: segment?.includes('cart') ? '#90BA2D' : '#666666'}}
                            icon={faPlus}/>
                        <span>Cart</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/order"}>
                        <FontAwesomeIcon
                            className={style.icon}
                            style={{color: segment?.includes('order') ? '#90BA2D' : '#666666'}}
                            icon={faGripLines}/>
                        <span>Request Order</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/result"}>
                        <FontAwesomeIcon
                            className={style.icon}
                            style={{color: segment?.includes('result') ? '#90BA2D' : '#666666'}}
                            icon={faCircleCheck}/>
                        <span>Result</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/qna"}>
                        <FontAwesomeIcon
                            className={style.icon}
                            style={{color: segment?.includes('qna') ? '#90BA2D' : '#666666'}}
                            icon={faCircleQuestion}/>
                        <span>QnA</span>
                    </Link>
                </li>
            </ul>
        </div>
    )
}