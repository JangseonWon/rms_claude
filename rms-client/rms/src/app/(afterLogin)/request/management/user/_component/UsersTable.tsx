"use client"

import React, {useEffect, useState} from "react";
import globalTableStyle from "@/css/globalTable.module.css";
import managementStyle from "@/css/managementTable.module.css";
import {faAngleLeft, faAngleRight, faMagnifyingGlass} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Query} from "@/model/Query";
import {User} from "@/model/User";
import {postUsers} from "@/app/(afterLogin)/request/management/user/_api/postUsers";
import SwitchButton from "@/app/_component/SwitchButton";
import {fetchUserUpdate} from "@/app/(afterLogin)/_api/fetchUserUpdate";
import InstitutionModal from "@/app/(afterLogin)/request/management/user/_component/InstitutionModal";
import UserEditModal from "@/app/(afterLogin)/request/management/user/_component/UserEditModal";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import RectangleButton from "@/app/_component/RectangleButton";
import BlueButton from "@/app/_component/BlueButton";
import {putAlisUsers} from "@/app/(afterLogin)/request/management/user/_api/putAlisUsers";
import LoadingFullScreen from "@/app/_component/LoadingFullScreen";

interface UserWithSelected extends User {
    isSelected?: boolean;
}
const selectBoxOptions: SelectBoxOption[] = [
    { table: "user", column: "id", name: "ID" },
    { table: "user", column: "name", name: "Name" },
    { table: "user", column: "email", name: "Email" },
    { table: "user", column: "phone_number", name: "Phone Number" },
    { table: "user", column: "branch_serial", name: "Serial" },
    { table: "user", column: "role", name: "Role" }
];

export default function UsersTable() {
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [userData, setUserData] = useState<UserWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>({sort_by:"id", asc: true, size:10, page:1});
    const [userServiceModalOpen, setUserServiceModalOpen] = useState<boolean>(false);
    const [selectedUser, setSelectedUser] = useState<User>();
    const [institutionModalOpen, setInstitutionModalOpen] = useState<boolean>(false);
    const [selectedInstitutionUser, setSelectedInstitutionUser] = useState<{id: string; name: string} | null>(null);
    const [selectOption, setSelectOption] = useState<SelectBoxOption>(selectBoxOptions[0]);

    const handlePageChange = (newPageNumber: number) => {
        setSearch(prevPage =>({
            ...prevPage,
            page: newPageNumber
        }));
    };
    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value);
        setSearch(prevSearch => ({
            ...prevSearch,
            size: newSize,
            page: 1
        }));
    };

    const handleSearchChange = (option: SelectBoxOption, value: string) => {
        setSearch((prevSearch) => ({
            ...prevSearch,
            filter_groups:[
                {
                    filters: [
                        {
                            table: option.table!,
                            column: option.column!,
                            value: value,
                            operator: "LIKE"
                        }
                    ]
                }
            ],
            page:1
        }));
    };


    const fetchData = async (search: Query) => {
        try {
            const response = await postUsers(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setUserData(responseData as User[]);
            setTotalPage(totalPage)
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setUserData([]);
            setTotalPage(0);
        }
    }

    const handleToggle = async (id: string, checked: boolean) => {
        const state = checked ? 'ACTIVE' : 'INACTIVE';
        const userUpdate = { id, state };
        await fetchUserUpdate(userUpdate);
        await fetchData(search);
    };

    const handleAlisSyncButtonClick = async(search: Query) => {
        try {
            setIsLoading(true)
            const response = await putAlisUsers(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            setUserData(responseData as User[]);
            setTotalPage(totalPage)
            if(response.ok){
                await fetchData(search)
                alert("sync success!")
            }
        } catch(error) {
            alert(`fail: ${error}`)
        } finally {
            setIsLoading(false)
        }
    }

    const handleInstitutionIconClick = (id: string, name: string | undefined) => {
        setSelectedInstitutionUser({ id, name: name ?? '' });
        setInstitutionModalOpen(true);
    }

    const handleUserEditClick = (user: User) => {
        setSelectedUser(user);
        setUserServiceModalOpen(true);
    }

    const closeModal = () => {
        setInstitutionModalOpen(false);
        setUserServiceModalOpen(false);
        setSelectedInstitutionUser(null);
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <div>
            {isLoading && <LoadingFullScreen/>}
            <section className={managementStyle.filterContainer}>
                <div className={managementStyle.filterContainerAlis}>
                    <div className={managementStyle.alisSyncButton}>
                        <BlueButton name={"Alis-Sync"} onClick={() => handleAlisSyncButtonClick(search)}/>
                    </div>
                </div>
                <div className={managementStyle.filterContainerSearch}>
                    <SelectBox
                        width={"7vw"}
                        value={selectOption.name}
                        options={selectBoxOptions}
                        label={"status"}
                        onChange={(selectedOption) => {
                            setSelectOption(selectedOption);
                        }}
                    />
                    <div className={managementStyle.search}>
                        <InputBox label={"search"} onChange={(value) => {
                            handleSearchChange(selectOption, value)}}>
                        </InputBox>
                    </div>
                </div>
            </section>
            <section className={globalTableStyle.tableContainer}>
                <table className={globalTableStyle.table}>
                    <thead>
                    <tr>
                        <th>Id</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone Number</th>
                        <th>Serial</th>
                        <th>Role</th>
                        <th>Institution</th>
                        <th>State</th>
                        <th>Edit</th>
                    </tr>
                    </thead>
                    <tbody>
                    {userData && userData.length > 0 && userData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.id}</td>
                            <td>{row.name}</td>
                            <td>{row.email}</td>
                            <td>{row.phone_number}</td>
                            <td>{row.branch_serial}</td>
                            <td>{row.role}</td>
                            <td>
                                <FontAwesomeIcon
                                    className={globalTableStyle.icon}
                                    icon={faMagnifyingGlass}
                                    onClick={() => handleInstitutionIconClick(row.id, row.name)}
                                />
                            </td>
                            <td>
                                <SwitchButton
                                    id={row.id}
                                    checked={row.state === 'ACTIVE'}
                                    onToggle={handleToggle}
                                />
                            </td>
                            <td>
                                <RectangleButton name={'Edit'} onClick={() => handleUserEditClick(row)}/>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </section>
            <div className={globalTableStyle.pagination}>
                <span>items per page:</span>
                <div className={globalTableStyle.select}>
                    <select onChange={handlePageSizeChange}>
                        <option value="10">10</option>
                        <option value="20">20</option>
                        <option value="50">50</option>
                    </select>
                </div>
                <span> 1-{totalPage} of {search.page} </span>
                <button
                    disabled={search.page === 1}
                    onClick={() => handlePageChange((search.page ?? 1) - 1)}
                ><FontAwesomeIcon icon={faAngleLeft}/>
                </button>
                <button
                    disabled={search.page === totalPage}
                    onClick={() => handlePageChange((search.page ?? 1) + 1)}
                ><FontAwesomeIcon icon={faAngleRight}/>
                </button>
            </div>
            {selectedInstitutionUser && (
                <InstitutionModal id={selectedInstitutionUser.id} name={selectedInstitutionUser.name}
                                  open={institutionModalOpen} closeModal={closeModal}/>
            )}
            {userServiceModalOpen && (
                <UserEditModal userId={selectedUser!.id} closeModal={closeModal}/>
            )}
        </div>
    );
}