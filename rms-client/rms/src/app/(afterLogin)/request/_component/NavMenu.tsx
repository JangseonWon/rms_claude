"use client"

import style from "@/app/(afterLogin)/request/_component/navMenu.module.css"
import Link from "next/link";
import {
    faBorderAll,
    faCircleCheck,
    faGripLines,
    faHouse,
    faList,
    faPlus,
    faTableColumns
} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useState} from "react";
import {useSelectedLayoutSegment} from "next/navigation";


export default function NavMenu() {
    const segment = useSelectedLayoutSegment();
    const [showServicesDropdown, setShowServicesDropdown] = useState(false);
    const [showResultDropdown, setShowResultDropdown] = useState(false);

    const toggleServicesDropdown = () => {
        setShowServicesDropdown(!showServicesDropdown);
    }
    const toggleResultDropdown = () => {
        setShowResultDropdown(!showResultDropdown);
    }
    return (
        <li className={style.navPill}>
            <ul>
                <li>
                    <Link href={"/home"}>
                        <FontAwesomeIcon
                            className={segment === 'home' ? style.clickIcon : style.icon}
                            icon={faHouse}/>
                        <span className={segment === 'home' ? style.clickSpan : ''}>Home</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/dashboard"}>
                        <FontAwesomeIcon
                            className={segment === 'dashboard' ? style.clickIcon : style.icon}
                            icon={faTableColumns}/>
                        <span className={segment === 'dashboard' ? style.clickSpan : ''}>Dashboard</span>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/service-catalog"}>
                        <FontAwesomeIcon
                            className={segment === '/request/service-catalog' ? style.clickIcon : style.icon}
                            icon={faList}/>
                        <span className={segment === 'service-catalog' ? style.clickSpan : ''}>Service Catalog</span>
                    </Link>
                </li>
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
                <li onClick={toggleResultDropdown}>
                    <FontAwesomeIcon
                        className={segment === 'result' ? style.clickIcon : style.icon}
                        icon={faCircleCheck}/>
                    <span className={segment === 'result' ? style.clickSpan : ''}>Result</span>
                </li>
                {showResultDropdown && (
                    <>
                        <ol>
                            <Link href={"/request/result/download"}>
                                Download
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/result/resample"}>
                                Re-sample
                            </Link>
                        </ol>
                    </>
                )}
            </ul>
        </li>
    )
}