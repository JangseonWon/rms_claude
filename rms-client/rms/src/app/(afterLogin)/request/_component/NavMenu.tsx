"use client"

import style from "@/app/(afterLogin)/request/_component/navMenu.module.css"
import Link from "next/link";
import {faHouse, faAngleDown, faGripLines, faBorderAll, faPlus, faCircleCheck, faCircleQuestion} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React from "react";

export default function NavMenu() {

    return (
        <div className={style.navPill}>
            <ul>
                <li>
                    <Link href={"/request/dashboard"}>
                        <FontAwesomeIcon icon={faHouse}/>
                        <span>Dashboard</span>
                        <FontAwesomeIcon icon={faAngleDown}/>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/services"}>
                        <FontAwesomeIcon icon={faBorderAll}/>
                        <span>Services</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/cart"}>
                        <FontAwesomeIcon icon={faPlus}/>
                        <span>Cart</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/order"}>
                        <FontAwesomeIcon icon={faGripLines}/>
                        <span>Request Order</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/result"}>
                        <FontAwesomeIcon icon={faCircleCheck}/>
                        <span>Result</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/qna"}>
                        <FontAwesomeIcon icon={faCircleQuestion}/>
                        <span>QnA</span>
                    </Link>
                </li>
            </ul>
        </div>
    )
}