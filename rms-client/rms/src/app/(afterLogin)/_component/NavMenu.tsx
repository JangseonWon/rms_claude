"use client"

import style from "@/app/(afterLogin)/_component/navMenu.module.css"
import Link from "next/link";
import {
    faCircleCheck,
    faGripLines,
    faHouse,
    faList,
    faPlus,
    faTableColumns,
    faUsers
} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useState} from "react";
import {useSelectedLayoutSegment} from "next/navigation";
import {useSession} from "next-auth/react";

export default function NavMenu() {
    const segment = useSelectedLayoutSegment();
    const [showResultDropdown, setShowResultDropdown] = useState(false);
    const [showManagementDropdown, setShowManagementDropdown] = useState(false);
    const { data: session } = useSession();

    const toggleResultDropdown = () => {
        setShowResultDropdown(!showResultDropdown);
    }

    const toggleManagementDropdown = () => {
        setShowManagementDropdown(!showManagementDropdown);
    }

    return (
        <li className={style.navPill}>
            <ul>
                <li>
                    <Link href={"/home"}>
                        <div className={style.navItem}>
                            <FontAwesomeIcon
                                className={segment === 'home' ? style.clickIcon : style.icon}
                                icon={faHouse}/>
                            <span className={segment === 'home' ? style.clickSpan : ''}>Home</span>
                        </div>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/dashboard"}>
                        <div className={style.navItem}>
                            <FontAwesomeIcon
                                className={segment === 'dashboard' ? style.clickIcon : style.icon}
                                icon={faTableColumns}/>
                            <span className={segment === 'dashboard' ? style.clickSpan : ''}>Dashboard</span>
                        </div>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/service-catalog"}>
                        <div className={style.navItem}>
                            <FontAwesomeIcon
                                className={segment === '/request/service-catalog' ? style.clickIcon : style.icon}
                                icon={faList}/>
                            <span className={segment === 'service-catalog' ? style.clickSpan : ''}>Service Catalog</span>
                        </div>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/cart"}>
                        <div className={style.navItem}>
                            <FontAwesomeIcon
                                className={segment === 'cart' ? style.clickIcon : style.icon}
                                icon={faPlus}/>
                            <span className={segment === 'cart' ? style.clickSpan : ''}>Cart</span>
                        </div>
                    </Link>
                </li>
                <li>
                    <Link href={"/request/order"}>
                        <div className={style.navItem}>
                            <FontAwesomeIcon
                                className={segment === 'order' ? style.clickIcon : style.icon}
                                icon={faGripLines}/>
                            <span className={segment === 'order' ? style.clickSpan : ''}>Request Order</span>
                        </div>
                    </Link>
                </li>
                <li onClick={toggleResultDropdown}>
                    <div className={style.navItem}>
                        <FontAwesomeIcon
                            className={segment === 'result' ? style.clickIcon : style.icon}
                            icon={faCircleCheck}/>
                        <span className={segment === 'result' ? style.clickSpan : ''}>Result</span>
                    </div>
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
                {session?.user.role !== 'USER' && (
                    <li onClick={toggleManagementDropdown}>
                        <div className={style.navItem}>
                            <FontAwesomeIcon
                                className={segment === 'management' ? style.clickIcon : style.icon}
                                icon={faUsers}/>
                            <span className={segment === 'management' ? style.clickSpan : ''}>Management</span>
                        </div>
                    </li>
                    )}
                {showManagementDropdown && session?.user.role !== 'USER' && (
                    <>
                        <ol>
                            <Link href={"/request/management/user"}>
                                User Management
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/management/service"}>
                                Service Management
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/management/additional-info"}>
                                Additional info Management
                            </Link>
                        </ol>
                    </>
                )}
            </ul>
        </li>
    )
}