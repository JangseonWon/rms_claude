"use client"

import style from "./profileButton.module.css"
import {Session} from "@auth/core/types";
import React, {useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import { faUser } from "@fortawesome/free-regular-svg-icons";
import { faChevronDown } from "@fortawesome/free-solid-svg-icons";
import {useRouter} from "next/navigation";
import {signOut} from "next-auth/react";

type Props = {
    session: Session | null
}

export default function ProfileButton({session}: Props) {
    const [showDropdown, setShowDropdown] = useState(false);
    const router = useRouter()

    const onLogout = () =>{
        signOut({redirect: false})
            .then(() =>{
                router.replace('/login')
            })
    }

    const toggleDropdown = () => {
        setShowDropdown((prev) => !prev);
    };
    return(
        <>
            <div className={style.profile} onClick={toggleDropdown}>
                <FontAwesomeIcon icon={faUser} />
                <span>{session?.user?.name}</span>
                <FontAwesomeIcon icon={faChevronDown} />
            </div>
            {showDropdown && (
                <div className={style.profileDropdown}>
                    <ul>
                        <li>My profile</li>
                        <li>Manage the institution</li>
                        <li>Setting</li>
                        <li onClick={onLogout}>Logout</li>
                    </ul>
                </div>
            )}
        </>
    )
}