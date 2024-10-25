import style from "./serviceSearchBox.module.css";
import React, {useEffect, useRef, useState} from "react";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {useRouter} from "next/navigation";
import {Query} from "@/model/Query";
import {postServiceByUser} from "@/app/(afterLogin)/request/service-catalog/_api/postServiceByUser";
import {User} from "@/model/User";

export default function ServiceSearchBox() {
    const router = useRouter();
    const [isOpen, setIsOpen] = useState<boolean>(false)
    const [options, setOptions] = useState<SelectBoxOption[]>([]);
    const [search, setSearch] = useState<string>('');
    const selectBoxRef = useRef<HTMLDivElement>(null);

    const transformDataToOptions = (user: User): SelectBoxOption[] => {
        if (!user.services) {
            return [];
        }
        return user.services.map(value => ({
            value: value.id,
            name: value.name
        }));
    };

    const toggleList = () => {
        setIsOpen(!isOpen)
    }

    const handleOptionClick = (option: SelectBoxOption) => {
        setSearch(option.name!);
        router.push(`/request/services/${option.value}/single`);
        setIsOpen(!isOpen);
    };

    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearch(event.target.value);
    };

    const fetchOptions = async () => {
        setOptions([]);
        const query: Query = {
            filter_groups: [
                {
                    condition_type: "AND",
                    filters: [
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
        const response = await postServiceByUser(query);
        const data = response.ok && response.headers.get("Content-Length") !== "0" ? await response.json() : null;
        if (data) {setOptions(transformDataToOptions(data));}
        else {setOptions([]);}
    };

    const handleClickOutside = (event: MouseEvent) => {
        if (selectBoxRef.current && !selectBoxRef.current.contains(event.target as Node)) {
            setIsOpen(false);
        }
    };

    useEffect(() => {
        fetchOptions();
    }, [search]);

    useEffect(() => {
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    return (
        <div ref={selectBoxRef} className={style.container} onClick={toggleList}>
            <section className={style.selectSection}>
                <p className={style.label}>Search the service</p>
                <div className={`${style.btnSelect} ${isOpen ? style.open : ''}`}>
                    <input
                        className={style.selectInput}
                        type="text"
                        value={search}
                        onChange={handleSearchChange}
                    />
                    <FontAwesomeIcon icon={faChevronDown} className={style.icon}/>
                </div>
                <div className={`${style.searchList} ${isOpen ? style.open : ''}`}>
                    <ul className={style.listMember}>
                        {options.map((option) => (
                            <li key={option.name}>
                                <button onClick={() => handleOptionClick(option)}>
                                    {option.name}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            </section>
        </div>
    )
}