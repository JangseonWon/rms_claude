"use client"

import style from "./profileButton.module.css"
import React, {useEffect, useRef, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBell, faUser} from "@fortawesome/free-regular-svg-icons";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {useRouter} from "next/navigation";
import {signOut, useSession} from "next-auth/react";
import ProfileAlarm from "@/app/(afterLogin)/_component/alarm/ProfileAlarm";
import {getAlarmCountByUser} from "@/app/(afterLogin)/_api/getAlarmCountByUser";
import {useAlarmCount, useSetAlarmCount} from "@/app/(afterLogin)/_component/alarm/store/useAlarmCountStore";

export default function ProfileButton() {
    const { data: session } = useSession();
    const [profileOpen, setProfileOpen] = useState(false);
    const [alarmOpen, setAlarmOpen] = useState(false);
    const alarmCount = useAlarmCount();
    const setAlarmCount = useSetAlarmCount();
    const router = useRouter()
    const alarmRef = useRef<HTMLDivElement>(null);

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

    const handleClickOutside = (event: MouseEvent) => {
        if (alarmRef.current && !alarmRef.current.contains(event.target as Node)) {
            setAlarmOpen(false);
        }
    };

    useEffect(() => {
        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    const fetchAlarmCount = async () => {
        const response = await getAlarmCountByUser();
        const data = await response.json();
        setAlarmCount(data as number);
    };

    useEffect(() => {
        fetchAlarmCount();

        const interval = setInterval(() => {
            fetchAlarmCount();
        }, 60000);

        return () => clearInterval(interval);
    }, [alarmOpen]);

    return(
        <div className={style.container}>
            <div className={style.bellContainer} onClick={handleAlarmToggle}>
                <FontAwesomeIcon className={`${style.bell} ${alarmOpen ? style.bellActive : ''}`} icon={faBell}/>
                {alarmCount > 0 && (
                    <span className={style.alarmCount}>{alarmCount}</span>
                )}
            </div>
            {alarmOpen && (
                <div className={`${style.profileAlarm} active`}>
                    <ProfileAlarm/>
                </div>
            )}
            <div className={style.middle}></div>
            <div className={style.profileContainer} onMouseEnter={handleUserMouseEnter}
                 onMouseLeave={handleUserMouseLeave}>
                <div className={style.profile}>
                    <FontAwesomeIcon className={style.user} icon={faUser}/>
                    <div className={style.userName}>
                        <span>{session?.user?.name}</span>
                    </div>
                    <FontAwesomeIcon className={style.ChevronDown} icon={faChevronDown}/>
                </div>
                {profileOpen && (
                    <div className={style.profileDropdown} onMouseEnter={handleUserMouseEnter}
                         onMouseLeave={handleUserMouseLeave}>
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