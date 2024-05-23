"use client"

import style from "./profileButton.module.css"
import {Session} from "@auth/core/types";
import React, {useEffect, useRef, useState} from "react";
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
            .then(() =>{
                document.cookie = "Authorization=;path=/;";
            })
            .then(() =>{
                router.replace('/login')
            })
    }

    const onProfile = () =>{
        router.push('/user')
    }

    const toggleDropdown = () => {
        setShowDropdown((prev) => !prev);
    };

    const handleClickOutside = (event: MouseEvent) => {
        if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
            setShowDropdown(false);
        }
    };

    useEffect(() => {
        if (showDropdown) {
            document.addEventListener("mousedown", handleClickOutside);
        } else {
            document.removeEventListener("mousedown", handleClickOutside);
        }
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [showDropdown]);

    return(
        <>
            <div className={style.profile} onClick={toggleDropdown}>
                <FontAwesomeIcon icon={faUser} />
                <span>{session?.user?.name}</span>
                <FontAwesomeIcon icon={faChevronDown} />
            </div>
            {showDropdown && (
                <div className={style.profileDropdown} ref={dropdownRef}>
                    <ul>
                        <li onClick={onProfile}>My profile</li>
                        <li>Manage the institution</li>
                        <li>Setting</li>
                        <li onClick={onLogout}>Logout</li>
                    </ul>
                </div>
            )}
        </>
    )
}