"use client"

import style from "./profileButton.module.css"
import React, {useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBell, faUser} from "@fortawesome/free-regular-svg-icons";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {useRouter} from "next/navigation";
import {signOut, useSession} from "next-auth/react";
import ProfileAlarm from "@/app/(afterLogin)/_component/ProfileAlarm";

export default function ProfileButton() {
    const { data: session } = useSession();
    const [profileOpen, setProfileOpen] = useState(false);
    const [alarmOpen, setAlarmOpen] = useState(false);
    const router = useRouter()

    const onLogout = () =>{
        signOut({redirect: false})
            .then(() =>{document.cookie = "Authorization=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";})
            .then(() =>{router.replace('/login')})
    }

    const onProfile = () =>{
        router.push('/user')
    }

    const handleAlarmToggle = () => {
        setAlarmOpen(!alarmOpen);
    };

    const handleUserMouseEnter = () => {
        setProfileOpen(true);
    };

    const handleUserMouseLeave = () => {
        setProfileOpen(false);
    };

    return(
        <div className={style.container}>
            <div className={style.profile} onClick={handleAlarmToggle}>
                <FontAwesomeIcon className={`${style.bell} ${alarmOpen ? style.bellActive : ''}`} icon={faBell}/>
            </div>
            {alarmOpen && (
                <div className={`${style.profileAlarm} active`}>
                    <ProfileAlarm />
                </div>
            )}
            <div className={style.profileContainer} onMouseEnter={handleUserMouseEnter} onMouseLeave={handleUserMouseLeave}>
                <div className={style.profile}>
                    <FontAwesomeIcon className={style.user} icon={faUser}/>
                    <div className={style.userName}>
                        <span>{session?.user?.name}</span>
                    </div>
                    <FontAwesomeIcon className={style.ChevronDown} icon={faChevronDown}/>
                </div>
                {profileOpen && (
                    <div className={style.profileDropdown} onMouseEnter={handleUserMouseEnter} onMouseLeave={handleUserMouseLeave}>
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