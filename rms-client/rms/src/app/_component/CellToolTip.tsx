import React, {useState} from "react";

export default function CellTooltip({ text }: { text?: string }) {
    const [visible, setVisible] = useState(false);
    const [position, setPosition] = useState({ x: 0, y: 0 });

    const handleMouseEnter = (e: React.MouseEvent) => {
        const { clientX, clientY } = e;
        setPosition({ x: clientX, y: clientY });
        setVisible(true);
    };

    const handleMouseLeave = () => {
        setVisible(false);
    };

    return (
        <>
            <span
                onMouseEnter={handleMouseEnter}
                onMouseLeave={handleMouseLeave}
                style={{ cursor: "default" }}
            >
                {text}
            </span>
            {visible && (
                <div
                    style={{
                        position: "fixed",
                        top: position.y - 30,
                        left: position.x,
                        background: "#333",
                        color: "#fff",
                        padding: "5px 10px",
                        borderRadius: "5px",
                        fontSize: "12px",
                        zIndex: 1000
                    }}
                >
                    {text}
                </div>
            )}
        </>
    );
}