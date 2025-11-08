package Repository;

import Data.DataConnection;
import Model.CaLam;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaLamRepository {
    
    public List<CaLam> getAll() throws SQLException {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam ORDER BY NgayLam DESC, GioBatDau ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToCaLam(rs));
            }
        }
        return list;
    }
    
    public CaLam getById(int maCa) throws SQLException {
        String sql = "SELECT * FROM CaLam WHERE MaCa = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maCa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCaLam(rs);
                }
            }
        }
        return null;
    }
    
    public List<CaLam> getByMaNhanVien(int maNhanVien) throws SQLException {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam WHERE MaNhanVien = ? ORDER BY NgayLam DESC, GioBatDau ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCaLam(rs));
                }
            }
        }
        return list;
    }
    
    public List<CaLam> getByNgay(Date ngayLam) throws SQLException {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam WHERE NgayLam = ? ORDER BY GioBatDau ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, ngayLam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCaLam(rs));
                }
            }
        }
        return list;
    }
    
    public List<CaLam> getByThangNam(int thang, int nam) throws SQLException {
        List<CaLam> list = new ArrayList<>();
        // Access sử dụng MONTH() và YEAR() cho Date fields
        String sql = "SELECT * FROM CaLam WHERE MONTH(NgayLam) = ? AND YEAR(NgayLam) = ? ORDER BY NgayLam ASC, GioBatDau ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCaLam(rs));
                }
            }
        }
        return list;
    }
    
    public List<CaLam> getByKhoangThoiGian(Date tuNgay, Date denNgay) throws SQLException {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam WHERE NgayLam BETWEEN ? AND ? ORDER BY NgayLam ASC, GioBatDau ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, tuNgay);
            stmt.setDate(2, denNgay);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCaLam(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(CaLam caLam) throws SQLException {
        String sql = "INSERT INTO CaLam (MaNhanVien, NgayLam, GioBatDau, GioKetThuc, SoGioLam, SoGioTangCa, TienTip, NgayTao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setCaLamParameters(stmt, caLam);
            // Thêm NgayTao thủ công cho Access
            stmt.setTimestamp(8, Timestamp.valueOf(java.time.LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        caLam.setMaCa(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(CaLam caLam) throws SQLException {
        // Thêm NgayCapNhat vào câu SQL (thay thế trigger)
        String sql = "UPDATE CaLam SET MaNhanVien=?, NgayLam=?, GioBatDau=?, GioKetThuc=?, SoGioLam=?, " +
                    "SoGioTangCa=?, TienTip=?, NgayCapNhat=? WHERE MaCa=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setCaLamParameters(stmt, caLam);
            // Thêm NgayCapNhat thủ công cho Access (thay thế trigger)
            stmt.setTimestamp(8, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(9, caLam.getMaCa());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateTienTip(int maCa, BigDecimal tienTip) throws SQLException {
        // Thêm NgayCapNhat vào câu SQL (thay thế trigger)
        String sql = "UPDATE CaLam SET TienTip = ?, NgayCapNhat = ? WHERE MaCa = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, tienTip);
            // Thêm NgayCapNhat thủ công cho Access
            stmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(3, maCa);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateSoGioLam(int maCa, BigDecimal soGioLam, BigDecimal soGioTangCa) throws SQLException {
        // Thêm NgayCapNhat vào câu SQL (thay thế trigger)
        String sql = "UPDATE CaLam SET SoGioLam = ?, SoGioTangCa = ?, NgayCapNhat = ? WHERE MaCa = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, soGioLam);
            stmt.setBigDecimal(2, soGioTangCa);
            // Thêm NgayCapNhat thủ công cho Access
            stmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(4, maCa);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maCa) throws SQLException {
        String sql = "DELETE FROM CaLam WHERE MaCa = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maCa);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean exists(int maNhanVien, Date ngayLam, Time gioBatDau, Time gioKetThuc) throws SQLException {
        // Access không hỗ trợ CAST AS TIME, sử dụng trực tiếp Time values
        String sql = "SELECT COUNT(*) FROM CaLam " +
                    "WHERE MaNhanVien = ? AND NgayLam = ? " +
                    "AND ((GioBatDau <= ? AND GioKetThuc >= ?) " +
                    "OR (GioBatDau <= ? AND GioKetThuc >= ?))";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setDate(2, ngayLam);
            stmt.setTime(3, gioBatDau);
            stmt.setTime(4, gioBatDau);
            stmt.setTime(5, gioKetThuc);
            stmt.setTime(6, gioKetThuc);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean existsExcludingCurrent(int maNhanVien, Date ngayLam, Time gioBatDau, Time gioKetThuc, int maCaHienTai) throws SQLException {
        // Kiểm tra trùng lịch nhưng loại trừ ca hiện tại (dùng cho update)
        String sql = "SELECT COUNT(*) FROM CaLam " +
                    "WHERE MaNhanVien = ? AND NgayLam = ? AND MaCa != ? " +
                    "AND ((GioBatDau <= ? AND GioKetThuc >= ?) " +
                    "OR (GioBatDau <= ? AND GioKetThuc >= ?))";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setDate(2, ngayLam);
            stmt.setInt(3, maCaHienTai);
            stmt.setTime(4, gioBatDau);
            stmt.setTime(5, gioBatDau);
            stmt.setTime(6, gioKetThuc);
            stmt.setTime(7, gioKetThuc);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public BigDecimal getTongTienTipByNhanVien(int maNhanVien, Date tuNgay, Date denNgay) throws SQLException {
        String sql = "SELECT SUM(TienTip) FROM CaLam WHERE MaNhanVien = ? AND NgayLam BETWEEN ? AND ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setDate(2, tuNgay);
            stmt.setDate(3, denNgay);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal tongTienTip = rs.getBigDecimal(1);
                    return tongTienTip != null ? tongTienTip : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getTongGioLamByNhanVien(int maNhanVien, Date tuNgay, Date denNgay) throws SQLException {
        String sql = "SELECT SUM(SoGioLam) FROM CaLam WHERE MaNhanVien = ? AND NgayLam BETWEEN ? AND ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setDate(2, tuNgay);
            stmt.setDate(3, denNgay);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal tongGioLam = rs.getBigDecimal(1);
                    return tongGioLam != null ? tongGioLam : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
    
    private void setCaLamParameters(PreparedStatement stmt, CaLam caLam) throws SQLException {
        stmt.setInt(1, caLam.getMaNhanVien());
        stmt.setDate(2, Date.valueOf(caLam.getNgayLam()));
        stmt.setTime(3, Time.valueOf(caLam.getGioBatDau()));
        stmt.setTime(4, Time.valueOf(caLam.getGioKetThuc()));
        stmt.setBigDecimal(5, caLam.getSoGioLam());
        stmt.setBigDecimal(6, caLam.getSoGioTangCa());
        stmt.setBigDecimal(7, caLam.getTienTip());
    }
    
    private CaLam mapResultSetToCaLam(ResultSet rs) throws SQLException {
        // Xử lý các giá trị có thể null
        java.time.LocalDate ngayLam = rs.getDate("NgayLam") != null ? 
            rs.getDate("NgayLam").toLocalDate() : null;
            
        java.time.LocalTime gioBatDau = rs.getTime("GioBatDau") != null ? 
            rs.getTime("GioBatDau").toLocalTime() : null;
            
        java.time.LocalTime gioKetThuc = rs.getTime("GioKetThuc") != null ? 
            rs.getTime("GioKetThuc").toLocalTime() : null;
            
        java.time.LocalDateTime ngayTao = rs.getTimestamp("NgayTao") != null ? 
            rs.getTimestamp("NgayTao").toLocalDateTime() : null;
            
        java.time.LocalDateTime ngayCapNhat = rs.getTimestamp("NgayCapNhat") != null ? 
            rs.getTimestamp("NgayCapNhat").toLocalDateTime() : null;
        
        CaLam caLam = new CaLam(
            rs.getInt("MaCa"),
            rs.getInt("MaNhanVien"),
            ngayLam,
            gioBatDau,
            gioKetThuc,
            rs.getBigDecimal("SoGioLam"),
            rs.getBigDecimal("SoGioTangCa"),
            rs.getBigDecimal("TienTip"),
            ngayTao,
            ngayCapNhat
        );
        
        return caLam;
    }
}