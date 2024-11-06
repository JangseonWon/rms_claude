"use client"

import style from "./profileButton.module.css"
import React, {useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBell, faUser} from "@fortawesome/free-regular-svg-icons";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {useRouter} from "next/navigation";
import {signOut, useSession} from "next-auth/react";

export default function ProfileButton() {
    const { data: session } = useSession();
    const [showDropdown, setShowDropdown] = useState(false);
    const router = useRouter()

    const onLogout = () =>{
        signOut({redirect: false})
            .then(() =>{document.cookie = "Authorization=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";})
            .then(() =>{router.replace('/login')})
    }

    const onProfile = () =>{
        router.push('/user')
    }

    const handleMouseEnter = () => {
        setShowDropdown(true);
    };

    const handleMouseLeave = () => {
        setShowDropdown(false);
    };

    return(
        <div className={style.container}>
            <div className={style.profile}>
                <FontAwesomeIcon className={style.bell} icon={faBell}/>
            </div>
            <div className={style.profileContainer} onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
                <div className={style.profile}>
                    <FontAwesomeIcon className={style.user} icon={faUser}/>
                    <div className={style.userName}>
                        <span>{session?.user?.name}</span>
                    </div>
                    <FontAwesomeIcon className={style.ChevronDown} icon={faChevronDown}/>
                </div>
                {showDropdown && (
                    <div className={style.profileDropdown} onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
                        <ul>
                            <li onClick={onProfile}>My profile</li>
                            <li onClick={onLogout}>Logout</li>
                        </ul>
                    </div>
                )}
            </div>
        </div>
    )
}