'use client';

import React, {useEffect, useState} from "react";
import style from './userServiceEditModal.module.css';
import {faTrash, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectSearchBox from "@/app/(afterLogin)/request/management/_component/SelectSearchBox";
import {User} from "@/model/User";
import {getServicesByUserId} from "@/app/(afterLogin)/request/management/user/_api/getServicesByUserId";
import {Service} from "@/model/Service";
import {Filter} from "@/model/Filter";
import {deleteUserService} from "@/app/(afterLogin)/request/management/user/_api/deleteUserService";
import {putUserService} from "@/app/(afterLogin)/request/management/user/_api/putUserService";


type Props = {
    user: User;
    open: boolean;
    closeModal: () => void;
}

export default function UserServiceEditModal({user, open, closeModal}: Props) {
    const [serviceData, setServiceData] = useState<Service[]>();
    const [search, setSearch] = useState<string>('');
    const [selectedAddService, setSelectedAddService] = useState<SelectBoxOption | null>(null);

    const transformOptionToService = (option: SelectBoxOption): Service => {
        return {
            id: option.value,
            name: option.name
        }
    };

    const fetchServiceData = async (userId: string) => {
        const filter: Filter = { value: search };
        const response = await getServicesByUserId(userId, filter);
        const data = await response.json();
        setServiceData(data as Service[]);
    }

    const handleUserServiceInsertClick = async () => {
        if (selectedAddService) {
            const newService = transformOptionToService(selectedAddService);
            const response = await putUserService(user?.id, [newService]);
            if (response.ok) {
                alert('Service added successfully!');
            } else {
                alert('Failed to add service.');
            }
            await fetchServiceData(user?.id);
        }
    }

    const handleUserServiceDeleteClick = async (service: Service) => {
        const response = await deleteUserService(user?.id, [service]);
        if (response.ok) {
            alert('Service deleted successfully!');
        } else {
            alert('Failed to delete service.');
        }
        await fetchServiceData(user?.id!);
    }

    useEffect(() => {
        fetchServiceData(user?.id!);
    }, [search]);

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <section className={style.modalHeader}>
                    <div className={style.modalClose} onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </div>
                    <div className={style.modalTop}>
                        <div className={style.title}>
                            Edit User Service
                        </div>
                    </div>
                </section>
                <section className={style.modalBody}>
                    <section className={style.topBody}>
                        <div className={style.firstTop}>
                            <InputBox
                                label={"User Id"}
                                value={user.id}
                                disabled={true}
                            />
                            <InputBox
                                label={"User Name"}
                                value={user.name}
                                disabled={true}
                            />
                        </div>
                        <div className={style.secondTop}>
                            <div className={style.serviceAddContainer}>
                                <SelectSearchBox type={'service'} onSelect={setSelectedAddService} width={'15vw'}/>
                            </div>
                            <button className={style.addButton}
                                onClick={handleUserServiceInsertClick}
                            >
                                Add
                            </button>
                        </div>
                        <div className={style.thirdTop}>
                            <InputBox
                                label={"search"}
                                onChange={(value) => {setSearch(value)}}>
                            </InputBox>
                        </div>
                    </section>
                    <section className={style.bottomBody}>
                        <div className={style.tableBody}>
                            <div className={style.innerBody}>
                                <div className={style.serviceAddContainer}>
                                    <table className={style.table}>
                                        <thead>
                                        <tr>
                                            <th>Id</th>
                                            <th>Name</th>
                                            <th>Delete</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {serviceData?.map((service, index) => (
                                            <tr key={index}>
                                                <td>{service.id}</td>
                                                <td>{service.name}</td>
                                                <td>
                                                    <FontAwesomeIcon
                                                        className={style.deleteButton}
                                                        icon={faTrash}
                                                        onClick={() => handleUserServiceDeleteClick(service)}
                                                    />
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </section>
                </section>
            </div>
        </div>
    )
}