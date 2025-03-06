"use client"

import style from "./serviceTitle.module.css";
import {Service} from "@/model/Service";
import SingleMultiChangeButton from "@/app/(afterLogin)/request/services/_component/SingleMultiChangeButton";
import {ServiceType} from "@/model/ServiceType";
import {ExtensionType} from "@/model/Extension";

interface Props {
    serviceData?: Service;
}

export default function ServiceTitle({serviceData}: Props) {
    return (
        <div className={style.titleContainer}>
            <div className={style.title}>
                <div className={style.subTitle}>
                    Service &gt; <span>{serviceData?.name}</span>
                </div>
                <div className={style.mainTitle}>
                    {serviceData?.name}
                </div>
            </div>
            <div className={style.changeButton}>
                {!(serviceData?.type === ServiceType.SET ||
                    serviceData?.extensions?.some(extension => extension.type === ExtensionType.PROBAND_SEARCH)) && (
                    <SingleMultiChangeButton />
                )}
            </div>
        </div>
    )
}