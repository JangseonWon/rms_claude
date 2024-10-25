"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/user/_component/usersTable.module.css";
import {faAngleLeft, faAngleRight, faMagnifyingGlass} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Query} from "@/model/Query";
import {User} from "@/model/User";
import {postUsers} from "@/app/(afterLogin)/request/management/user/_api/postUsers";
import SwitchButton from "@/app/_component/SwitchButton";
import {fetchUserUpdate} from "@/app/(afterLogin)/_api/fetchUserUpdate";
import InstitutionModal from "@/app/(afterLogin)/request/management/user/_component/InstitutionModal";
import UserServiceEditModal from "@/app/(afterLogin)/request/management/user/_component/UserServiceEditModal";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import RectangleButton from "@/app/_component/RectangleButton";
import BlueButton from "@/app/_component/BlueButton";
import {Filter} from "@/model/Filter";
import {putAlisUsers} from "@/app/(afterLogin)/request/management/user/_api/putAlisUsers";
import {Service} from "@/model/Service";

interface UserWithSelected extends User {
    isSelected?: boolean;
}
const selectBoxOptions: SelectBoxOption[] = [
    { value: "id", name: "ID" },
    { value: "name", name: "Name" },
    { value: "email", name: "Email" },
    { value: "phone_number", name: "Phone Number" },
    { value: "branch_name", name: "Institution" },
    { value: "branch_serial", name: "Serial" },
    { value: "role", name: "Role" },
];

export default function UsersTable() {
    const [userData, setUserData] = useState<UserWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] = useState<Query>({sort_by:"id", asc: true, size:10, page:1});
    const [searchKey, setSearchKey] = useState<string>("id");
    const [searchValue, setSearchValue] = useState<string>("");
    const [serviceModalOpen, setServiceModalOpen] = useState<boolean>(false);
    const [userServiceModalOpen, setUserServiceModalOpen] = useState<boolean>(false);
    const [selectedUser, setSelectedUser] = useState<User>();
    const [institutionModalOpen, setInstitutionModalOpen] = useState<boolean>(false);
    const [selectedInstitutionUser, setSelectedInstitutionUser] = useState<{id: string; name: string} | null>(null);
    const [selectOption, setSelectOption] = useState<string>('ID');

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

    const handleSearchChange = (newFilter: { key: string; value: string }) => {
        setSearch((prevSearch) => ({
            ...prevSearch,
            page: 1,
            filter_groups: [
                {
                    condition_type: "AND",
                    filters: [
                        {
                            table: "user",
                            column: newFilter.key,
                            value: newFilter.value,
                            operator: "LIKE"
                        }
                    ]
                }
            ]
        }));
    };

    const handleSearchKeyChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const key = event.target.value;
        setSearchKey(key);
        handleSearchChange({key: key, value: searchValue});
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
        }
    }

    const handleInstitutionIconClick = (id: string, name: string | undefined) => {
        setSelectedInstitutionUser({ id, name: name ?? '' });
        setInstitutionModalOpen(true);
    }

    const handleUserServiceEditClick = (user: User) => {
        setSelectedUser(user);
        setUserServiceModalOpen(true);
    }

    const closeModal = () => {
        setServiceModalOpen(false);
        setInstitutionModalOpen(false);
        setUserServiceModalOpen(false);
        setSelectedInstitutionUser(null);
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.filterContainer}>
                <div className={style.filterContainerLeft}>
                    <div className={style.alisSyncButton}>
                        <BlueButton name={"Alis-Sync"} onClick={()=> handleAlisSyncButtonClick(search)}/>
                    </div>
                    <SelectBox
                        width={"7vw"}
                        value={selectOption}
                        options={selectBoxOptions}
                        label={"status"}
                        onChange={(selectedOption) => {
                            setSelectOption(selectedOption.value);
                            handleSearchKeyChange({target: {value: selectedOption.value}} as React.ChangeEvent<HTMLSelectElement>);
                        }}
                    />
                </div>
                <div className={style.filterContainerRight}>
                    <InputBox label={"search"} onChange={(value) => {
                        setSearchValue(value);
                        handleSearchChange({key: searchKey, value: value})
                    }}></InputBox>
                </div>
            </section>
            <section className={style.tableContainer}>
                <table className={style.table}>
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
                                    className={style.icon}
                                    icon={faMagnifyingGlass}
                                    onClick={()=> handleInstitutionIconClick(row.id, row.name)}
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
                                <RectangleButton name={'Edit'} onClick={() => handleUserServiceEditClick(row)}/>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <div className={style.pagination}>
                    <span>items per page:</span>
                    <div className={style.select}>
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
            </section>
            {selectedInstitutionUser && (
                <InstitutionModal id={selectedInstitutionUser.id} name={selectedInstitutionUser.name} open={institutionModalOpen} closeModal={closeModal}/>
            )}
            {userServiceModalOpen && (
                <UserServiceEditModal user={selectedUser!} open={serviceModalOpen} closeModal={closeModal}/>
            )}
        </>
    );
}