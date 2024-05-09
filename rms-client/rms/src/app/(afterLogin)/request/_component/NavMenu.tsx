"use client"

import style from "@/app/(afterLogin)/request/_component/navMenu.module.css"
import Link from "next/link";
import {
    faAngleDown,
    faBorderAll,
    faCircleCheck,
    faCircleQuestion,
    faGripLines,
    faHouse,
    faPlus
} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useState} from "react";
import {useSelectedLayoutSegment} from "next/navigation";


export default function NavMenu() {
    const segment = useSelectedLayoutSegment();
    const [showServicesDropdown, setShowServicesDropdown] = useState(false);
    const [showDashboardDropdown, setShowDashboardDropdown] = useState(false);

    const toggleServicesDropdown = () => {
        setShowServicesDropdown(!showServicesDropdown);
    }
    const toggleDashboardDropdown = () => {
        setShowDashboardDropdown(!showDashboardDropdown);
    }
    return (
        <li className={style.navPill}>
            <ul>
                <li onClick={toggleDashboardDropdown}>
                    <FontAwesomeIcon
                        className={segment === 'dashboard' ? style.clickIcon : style.icon}
                        icon={faHouse}/>
                    <span className={segment === 'dashboard' ? style.clickSpan : ''}>Dashboard</span>
                </li>
                {showDashboardDropdown && (
                    <>
                        <ol>
                            <Link href={"/request/dashboard/service-catalog"}>
                                Service Catalog
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/dashboard/dashboard"}>
                                Dashboard
                            </Link>
                        </ol>
                    </>
                )}
                <li onClick={toggleServicesDropdown}>
                    <FontAwesomeIcon
                        className={segment === 'services' ? style.clickIcon : style.icon}
                        icon={faBorderAll}/>
                    <span className={segment === 'services' ? style.clickSpan : ''}>Services</span>
                </li>
                {showServicesDropdown && (
                    <>
                        <ol>
                            <Link href={"/request/services/precision-oncology"}>
                                Precision Oncology
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/services/pre-and-neonatal"}>
                                Pre & neonatal
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/services/rare-disease"}>
                                Rare disease
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/services/health-checkup"}>
                                Health Checkup
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/services/others"}>
                                Others
                            </Link>
                        </ol>
                    </>
                )}
                <li>
                    <Link href={"/request/cart"}>
                        <FontAwesomeIcon
                            className={segment === 'cart' ? style.clickIcon : style.icon}
                            icon={faPlus}/>
                        <span className={segment === 'cart' ? style.clickSpan : ''}>Cart</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/order"}>
                        <FontAwesomeIcon
                            className={segment === 'order' ? style.clickIcon : style.icon}
                            icon={faGripLines}/>
                        <span className={segment === 'order' ? style.clickSpan : ''}>Request Order</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/result"}>
                        <FontAwesomeIcon
                            className={segment === 'result' ? style.clickIcon : style.icon}
                            icon={faCircleCheck}/>
                        <span className={segment === 'result' ? style.clickSpan : ''}>Result</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/qna"}>
                        <FontAwesomeIcon
                            className={segment === 'qna' ? style.clickIcon : style.icon}
                            icon={faCircleQuestion}/>
                        <span className={segment === 'qna' ? style.clickSpan : ''}>QnA</span>
                    </Link>
                </li>
            </ul>
        </li>
    )
}