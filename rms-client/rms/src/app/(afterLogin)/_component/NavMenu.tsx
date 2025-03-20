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
import {usePathname, useSelectedLayoutSegment} from "next/navigation";
import {useSession} from "next-auth/react";
import {Role} from "@/model/Role";

export default function NavMenu() {
    const segment = useSelectedLayoutSegment();
    const pathname = usePathname();
    const [showResultDropdown, setShowResultDropdown] = useState(false);
    const [showManagementDropdown, setShowManagementDropdown] = useState(false);
    const [showRequestOrder, setShowRequestOrder] = useState(false);
    const { data: session } = useSession();

    const toggleResultDropdown = () => {
        setShowResultDropdown(!showResultDropdown);
    }

    const toggleManagementDropdown = () => {
        setShowManagementDropdown(!showManagementDropdown);
    }
    const toggleRequestOrderDropdown = () => {
        setShowRequestOrder(!showRequestOrder)
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
                {session?.user.role === Role.USER.valueOf() && (
                    <li>
                        <Link href={"/request/service-catalog"}>
                            <div className={style.navItem}>
                                <FontAwesomeIcon
                                    className={segment === '/request/service-catalog' ? style.clickIcon : style.icon}
                                    icon={faList}/>
                                <span
                                    className={segment === 'service-catalog' ? style.clickSpan : ''}>Service Catalog</span>
                            </div>
                        </Link>
                    </li>
                )}
                {session?.user.role === Role.USER.valueOf() && (
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
                )}
                {session?.user.role === Role.USER.valueOf() && (
                    <li onClick={toggleRequestOrderDropdown}>
                        <div className={style.navItem}>
                            <FontAwesomeIcon
                                className={segment === 'order' ? style.clickIcon : style.icon}
                                icon={faGripLines}/>
                            <span className={segment === 'order' ? style.clickSpan : ''}>Request Order</span>
                        </div>
                    </li>
                )}
                {showRequestOrder && session?.user.role === Role.USER.valueOf() &&(
                    <>
                        <ol>
                            <Link href={"/request/order/barcode"}>
                                <span className={pathname === "/request/order/barcode" ? style.activeLink : ""}>
                                    Print Barcode
                                </span>
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/order/confirm"}>
                                <span className={pathname === "/request/order/confirm" ? style.activeLink : ""}>
                                    Confirm Order
                                </span>
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/order/complete"}>
                                <span className={pathname === "/request/order/complete" ? style.activeLink : ""}>
                                    Complete Order
                                </span>
                            </Link>
                        </ol>
                    </>
                )}
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
                                <span className={pathname === "/request/result/download" ? style.activeLink : ""}>
                                    Download
                                </span>
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/result/resample"}>
                                <span className={pathname === "/request/result/resample" ? style.activeLink : ""}>
                                    Re-sample
                                </span>
                            </Link>
                        </ol>
                    </>
                )}
                {session?.user.role !== Role.USER.valueOf() && (
                    <li onClick={toggleManagementDropdown}>
                        <div className={style.navItem}>
                            <FontAwesomeIcon
                                className={segment === 'management' ? style.clickIcon : style.icon}
                                icon={faUsers}/>
                            <span className={segment === 'management' ? style.clickSpan : ''}>Management</span>
                        </div>
                    </li>
                    )}
                {showManagementDropdown && session?.user.role !== Role.USER.valueOf() && (
                    <>
                        <ol>
                            <Link href={"/request/management/request"}>
                                <span
                                    className={pathname === "/request/management/request" ? style.activeLink : ""}>
                                    Request Management
                                </span>
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/management/user"}>
                                <span className={pathname === "/request/management/user" ? style.activeLink : ""}>
                                    User Management
                                </span>
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/management/service"}>
                                <span className={pathname === "/request/management/service" ? style.activeLink : ""}>
                                    Service Management
                                </span>
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/management/additional-info"}>
                                <span
                                    className={pathname === "/request/management/additional-info" ? style.activeLink : ""}>
                                    Additional info Management
                                </span>
                            </Link>
                        </ol>
                        <ol>
                            <Link href={"/request/management/sample-type"}>
                                <span
                                    className={pathname === "/request/management/sample-type" ? style.activeLink : ""}>
                                    SampleType Management
                                </span>
                            </Link>
                        </ol>
                    </>
                )}
            </ul>
        </li>
    )
}