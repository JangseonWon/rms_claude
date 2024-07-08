'use client';

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/user/_component/serviceModal.module.css";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {getServices} from "@/app/(afterLogin)/request/management/user/_api/getServices";
import {ServiceExtension} from "@/model/ServiceExtension";
import {Service} from "@/model/Service";

type Props = {
    id: string;
    open: boolean;
    closeModal: () => void;
}

export default function ServiceModal({id, open, closeModal}: Props) {
    const [serviceData, setServiceData] = useState<ServiceExtension[]>([]);
    const [selectService, setSelectService] = useState<Service>();

    const fetchServiceData = async (userId: string) => {
        const response = await getServices(userId);
        const data = await response.json();
        setServiceData(data as ServiceExtension[]);
    }

    useEffect(() => {
        if (open) {
            fetchServiceData(id);
        }
    }, [id, open]);

    if (!open) return null;


    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <section className={style.modalHeader}>
                    <div className={style.modalClose} onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </div>
                    <div className={style.modalTop}>
                        <div className={style.title}>Service Details</div>
                        <div className={style.serviceTitle}>
                            User Id : {id} / Service Name : {selectService?.name || 'No Service Selected'}
                        </div>
                    </div>
                </section>
                <section className={style.modalBody}>
                    <div className={style.leftBody}>
                        <h2>Service</h2>
                        <table className={style.serviceTable}>
                            <thead>
                            <tr>
                                <th>Id</th>
                                <th>Name</th>
                            </tr>
                            </thead>
                            <tbody>
                            {serviceData && serviceData.length > 0 && serviceData.map((row, rowIndex) => (
                                <tr key={rowIndex} onClick={() => setSelectService({id: row.id, name: row.name})}>
                                    <td>{row.id}</td>
                                    <td>{row.name}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                    <div className={style.centerBody}>
                        <h2>Sample Type</h2>
                        <table className={style.sideTable}>
                            <thead>
                            <tr>
                                <th>Code</th>
                                <th>Name</th>
                            </tr>
                            </thead>
                            <tbody>
                            {selectService && (serviceData
                                .find(service => service.id === selectService.id)
                                ?.sample_types?.map((extension, index) => (
                                    <tr key={index}>
                                        <td>{extension.id}</td>
                                        <td>{extension.name}</td>
                                    </tr>
                                )))}
                            </tbody>
                        </table>
                    </div>
                    <div className={style.rightBody}>
                        <h2>Extension</h2>
                        <table className={style.sideTable}>
                            <thead>
                            <tr>
                                <th>Code</th>
                                <th>Name</th>
                                <th>Required</th>
                            </tr>
                            </thead>
                            <tbody>
                            {selectService && (serviceData
                                .find(service => service.id === selectService.id)
                                ?.extensions?.map((extension, index) => (
                                    <tr key={index}>
                                        <td>{extension.id}</td>
                                        <td>{extension.name}</td>
                                        <td>{extension.required ? "Yes" : "No"}</td>
                                    </tr>
                                )))}
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </div>
    )
}