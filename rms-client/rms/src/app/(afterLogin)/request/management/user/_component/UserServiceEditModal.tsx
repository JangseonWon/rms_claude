'use client';

import React, {useEffect, useState} from "react";
import style from './userServiceEditModal.module.css';
import {faTrash, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectSearchBox from "@/app/(afterLogin)/request/management/_component/SelectSearchBox";
import {User} from "@/model/User";
import {postUserServices} from "@/app/(afterLogin)/request/management/user/_api/postUserservices";
import {Service} from "@/model/Service";
import {deleteUserService} from "@/app/(afterLogin)/request/management/user/_api/deleteUserService";
import {putUserService} from "@/app/(afterLogin)/request/management/user/_api/putUserService";
import {Query} from "@/model/Query";


type Props = {
    user: User;
    open: boolean;
    closeModal: () => void;
}

export default function UserServiceEditModal({user, open, closeModal}: Props) {
    const [userData, setUserData] = useState<User>();
    const [search, setSearch] = useState<string>('');
    const [selectedAddService, setSelectedAddService] = useState<SelectBoxOption | null>(null);


    const fetchServiceData = async (userId: string) => {
        const query: Query = {
            filter_groups:[
                {
                    condition_type: "OR",
                    filters:[
                        {
                            table: "service",
                            column: "id",
                            value: search,
                            operator: "LIKE"
                        },
                        {
                            table: "service",
                            column: "name",
                            value: search,
                            operator: "LIKE"
                        }
                    ]
                }
            ]
        };
        const response = await postUserServices(userId, query);
        const data = await response.json();
        setUserData(data as User);
    }

    const handleUserServiceInsertClick = async () => {
        if (selectedAddService) {
            const response = await putUserService(user.id, selectedAddService.value);
            if (response.ok) {
                alert('Service added successfully!');
            } else {
                alert('Failed to add service.');
            }
            await fetchServiceData(user.id);
        }
    }

    const handleUserServiceDeleteClick = async (service: Service) => {
        const response = await deleteUserService(user.id, service.id!);
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
                            <div>
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
                                <div>
                                    <table className={style.table}>
                                        <thead>
                                        <tr>
                                            <th>Id</th>
                                            <th>Name</th>
                                            <th>Delete</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {userData?.services?.map((service, index) => (
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