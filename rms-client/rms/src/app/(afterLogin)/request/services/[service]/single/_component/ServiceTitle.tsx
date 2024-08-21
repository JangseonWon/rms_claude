"use client"

import style from "./serviceTitle.module.css";
import {usePathname} from "next/navigation";
import {useEffect, useState} from "react";
import {Service} from "@/model/Service";
import {getService} from "@/app/(afterLogin)/_api/getService";

export default function ServiceTitle() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [serviceData, setServiceData] = useState<Service>();

    useEffect(() => {
        const fetchData = async () => {
            const response = await getService(serviceId);
            const data = await response.json();
            setServiceData(data);
        };
        fetchData();
    }, []);

    return (
        <div className={style.title}>
            <div className={style.subTitle}>
                Service &gt; <span>{serviceData?.name}</span>
            </div>
            <div className={style.mainTitle}>
                {serviceData?.name}
            </div>
        </div>
    )
}