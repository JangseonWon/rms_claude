"use client"

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/user/_component/usersTable.module.css";
import {faAngleLeft, faAngleRight, faMagnifyingGlass} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {Paging} from "@/model/Paging";
import {User} from "@/model/User";
import {getUsers} from "@/app/(afterLogin)/request/management/user/_api/getUsers";
import SwitchButton from "@/app/_component/SwitchButton";
import {fetchUserUpdate} from "@/app/(afterLogin)/_api/fetchUserUpdate";
import ServiceModal from "@/app/(afterLogin)/request/management/user/_component/ServiceModal";
import InstitutionModal from "@/app/(afterLogin)/request/management/user/_component/InstitutionModal";
import UserServiceEditModal from "@/app/(afterLogin)/request/management/user/_component/UserServiceEditModal";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";

interface UserWithSelected extends User {
    isSelected?: boolean;
}

export default function UsersTable() {
    const [userData, setUserData] = useState<UserWithSelected[]>([]);
    const [totalPage, setTotalPage] = useState<number>(0);
    const [search, setSearch] =
        useState<Paging>({filters: [], sort_by:"name", asc: true, size:10, page:1});
    const [searchKey, setSearchKey] = useState<string>("id");
    const [searchValue, setSearchValue] = useState<string>("");
    const [serviceModalOpen, setServiceModalOpen] = useState<boolean>(false);
    const [userServiceModalOpen, setUserServiceModalOpen] = useState<boolean>(false);
    const [selectedServiceUserId, setSelectedServiceUserId] = useState<string | null>(null);
    const [selectedUser, setSelectedUser] = useState<User>();
    const [institutionModalOpen, setInstitutionModalOpen] = useState<boolean>(false);
    const [selectedInstitutionUser, setSelectedInstitutionUser] = useState<{id: string; name: string} | null>(null);
    const [selectOption, setSelectOption] = useState<string>('ID');

    const selectBoxOptions: SelectBoxOption[] = [
        { value: "id", name: "ID" },
        { value: "name", name: "Name" },
        { value: "email", name: "Email" },
        { value: "phone_number", name: "Phone Number" },
        { value: "branch_name", name: "Institution" },
        { value: "branch_serial", name: "Serial" },
        { value: "role", name: "Role" },
    ];

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
        const filterWithOperator = { ...newFilter, operator: "LIKE" };
        setSearch((prevSearch) => ({
            ...prevSearch,
            filters: [filterWithOperator],
            page: 1
        }));
    };

    const handleSearchKeyChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const key = event.target.value;
        setSearchKey(key);
        handleSearchChange({key: key, value: searchValue});
    };

    const fetchData = async (search: Paging) => {
        try {
            const response = await getUsers(search)
            const totalPage = parseInt(response.headers.get("X-Total-Page") || '0');
            const responseData = await response.json();
            const data = responseData.data;
            setUserData(data as User[]);
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

    const handleAlisSyncButtonClick = () => {
        alert('add click');
    }

    const handleInstitutionIconClick = (id: string, name: string | undefined) => {
        setSelectedInstitutionUser({ id, name: name ?? '' });
        setInstitutionModalOpen(true);
    }

    const handleServiceIconClick = (id: string) => {
        setSelectedServiceUserId(id);
        setServiceModalOpen(true);
    }

    const handleUserServiceEditClick = (user: User) => {
        setSelectedUser(user);
        setUserServiceModalOpen(true);
    }

    const closeModal = () => {
        setServiceModalOpen(false);
        setInstitutionModalOpen(false);
        setUserServiceModalOpen(false);
        setSelectedServiceUserId(null);
        setSelectedInstitutionUser(null);
    }

    useEffect(() => {
        fetchData(search)
    }, [search]);

    return (
        <>
            <section className={style.filterContainer}>
                <div className={style.filterContainerLeft}>
                    <button className={style.alisSync} onClick={handleAlisSyncButtonClick}>
                        Alis-Sync
                    </button>
                    <SelectBox
                        width={"7vw"}
                        value={selectOption}
                        options={selectBoxOptions}
                        label={"status"}
                        onChange={(selectedOption) =>{
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
                        <th>Service</th>
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
                                <FontAwesomeIcon
                                    className={style.icon}
                                    icon={faMagnifyingGlass}
                                    onClick={()=> handleServiceIconClick(row.id)}
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
                                <button
                                    className={style.editButton}
                                    onClick={() => handleUserServiceEditClick(row)}
                                >
                                    Edit
                                </button>
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
                        onClick={() => handlePageChange(search.page - 1)}
                    ><FontAwesomeIcon icon={faAngleLeft}/>
                    </button>
                    <button
                        disabled={search.page === totalPage}
                        onClick={() => handlePageChange(search.page + 1)}
                    ><FontAwesomeIcon icon={faAngleRight}/>
                    </button>
                </div>
            </section>
            {selectedServiceUserId && (
                <ServiceModal id={selectedServiceUserId} open={serviceModalOpen} closeModal={closeModal}/>
            )}
            {selectedInstitutionUser && (
                <InstitutionModal id={selectedInstitutionUser.id} name={selectedInstitutionUser.name} open={institutionModalOpen} closeModal={closeModal}/>
            )}
            {userServiceModalOpen && (
                <UserServiceEditModal user={selectedUser!} open={serviceModalOpen} closeModal={closeModal}/>
            )}
        </>
    );
}