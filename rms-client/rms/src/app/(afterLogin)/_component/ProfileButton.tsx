"use client"

import style from "./profileButton.module.css"
import {Session} from "@auth/core/types";
import React, {useRef, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUser} from "@fortawesome/free-regular-svg-icons";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {useRouter} from "next/navigation";
import {signOut} from "next-auth/react";

type Props = {
    session: Session | null
}

export default function ProfileButton({session}: Props) {
    const [showDropdown, setShowDropdown] = useState(false);
    const router = useRouter()
    const dropdownRef = useRef<HTMLDivElement>(null);

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
        <div className={style.profileContainer} onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
            <div className={style.profile}>
                <FontAwesomeIcon icon={faUser} />
                <span>{session?.user?.name}</span>
                <FontAwesomeIcon icon={faChevronDown} />
            </div>
            {showDropdown && (
                <div className={style.profileDropdown} ref={dropdownRef}>
                    <ul>
                        <li onClick={onProfile}>My profile</li>
                        <li>Setting</li>
                        <li onClick={onLogout}>Logout</li>
                    </ul>
                </div>
            )}
        </div>
    )
}